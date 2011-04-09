package me.xjunior.remoteworks.gui;

import java.io.File;
import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionMonitor;
import ch.ethz.ssh2.Session;

/**
 * The SSH-2 connection is established in this thread.
 * If we would not use a separate thread (e.g., put this code in
 * the event handler of the "Login" button) then the GUI would not
 * be responsive (missing window repaints if you move the window etc.)
 */
class ConnectionThread extends Thread
{
	String hostname;
	String username;
	KeyboardInteractioListener listener;
	Session sess;
	Connection conn;
	private ConnectionListener connListener;

	public ConnectionThread(String hostname, String username) {
		this.hostname = hostname;
		this.username = username;
		conn = new Connection(hostname);
		conn.addConnectionMonitor(new ConnectionMonitor() {
			public void connectionLost(Throwable arg0) {
				if (connListener != null)
					connListener.onDisconnect(ConnectionThread.this);
			}
		});
	}
	
	public String getHost() {
		return hostname;
	}

	public void run() {
		String[] hostkeyAlgos = me.xjunior.remoteworks.Main.database.getPreferredServerHostkeyAlgorithmOrder(hostname);

		if (hostkeyAlgos != null)
			conn.setServerHostKeyAlgorithms(hostkeyAlgos);

		try {
			conn.connect(new AdvancedVerifier());
	
			/*
			 * 
			 * AUTHENTICATION PHASE
			 * 
			 */
			boolean enableKeyboardInteractive = true;
			boolean enableDSA = true;
			boolean enableRSA = true;
	
			String lastError = null;
	
			while (true) {
				if ((enableDSA || enableRSA) && conn.isAuthMethodAvailable(username, "publickey")) {
					if (enableDSA) {
						File key = me.xjunior.remoteworks.Main.config.getIdDsaFile();
	
						if (key.exists()) {
							boolean res = conn.authenticateWithPublicKey(username, key, this.listener.onInteraction(lastError,
									"DSA Authentication", null, "Enter DSA private key password:", true));
	
							if (res == true)
								break;
	
							lastError = "DSA authentication failed.";
						}
						enableDSA = false; // do not try again
					}
	
					if (enableRSA) {
						File key = me.xjunior.remoteworks.Main.config.getIdRsaFile();
	
						if (key.exists()) {
							boolean res = conn.authenticateWithPublicKey(username, key, this.listener.onInteraction(lastError,
									"RSA Authentication", null, "Enter RSA private key password:", true));
	
							if (res == true)
								break;
	
							lastError = "RSA authentication failed.";
						}
						enableRSA = false; // do not try again
					}
	
					continue;
				}
	
				if (enableKeyboardInteractive && conn.isAuthMethodAvailable(username, "keyboard-interactive")) {
					SSHInteraction sshi = new SSHInteraction(lastError);
					sshi.setKeyboardInteractionListener(listener);
	
					boolean res = conn.authenticateWithKeyboardInteractive(username, sshi);
	
					if (res == true)
						break;
	
					if (sshi.getPromptCount() == 0) {
						lastError = "Keyboard-interactive does not work.";
	
						enableKeyboardInteractive = false; // do not try this again
					} else {
						lastError = "Keyboard-interactive auth failed."; // try again, if possible
					}
	
					continue;
				}
	
				if (conn.isAuthMethodAvailable(username, "password"))
				{
					String pass = this.listener.onInteraction(lastError,
							"Password Authentication", null, "Enter password for " + username, true);
	
					if (pass == null)
						throw new IOException("Login aborted by user");
	
					boolean res = conn.authenticateWithPassword(username, pass);
	
					if (res == true)
						break;
	
					lastError = "Password authentication failed."; // try again, if possible
	
					continue;
				}
	
				throw new IOException("No supported authentication methods available.");
			}
		
			connListener.onConnect(this);
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}
	
	public Session getSession() throws IOException {
		if (sess == null) {
			sess = conn.openSession();
		}
		
		return sess;
	}
	
	public boolean sendMessage(String msg) throws IOException {
		for (char b : msg.toCharArray()) {
			getSession().getStdin().write(b);
			try {
				sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	public void setKeyboardInteractionListener(KeyboardInteractioListener listener) {
		this.listener = listener;
	}
	
	public void setConnectionListener(ConnectionListener connectionListener) {
		this.connListener = connectionListener;
	}

	public void close() {
		conn.close();
	}
	
	public interface ConnectionListener {
		public void onConnect(ConnectionThread conn);
		public void onDisconnect(ConnectionThread conn);
	}
}