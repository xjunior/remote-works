/**
 * This class is a refactoring of the demo terminal which cames with the
 * Ganymed SSH-2 library. Credits are 90% from them.
 */

package me.xjunior.remoteworks.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ch.ethz.ssh2.Session;

public class TerminalArea extends JPanel {
	private static final long serialVersionUID = 1L;

	ConnectionThread conn;
	InputStream in;
	OutputStream out;
	TextTransfer textTransfer = new TextTransfer();
	JTextArea terminalOutput = new JTextArea();

	int x, y;

	class RemoteConsumer extends Thread
	{
		char[][] lines = new char[y][];
		int posy = 0;
		int posx = 0;

		private void addText(byte[] data, int len)
		{
			for (int i = 0; i < len; i++) {
				char c = (char) (data[i] & 0xff);

				if (c == 8) {// Backspace, VERASE
					if (posx < 0)
						continue;
					posx--;
					continue;
				}

				if (c == '\r') {
					posx = 0;
					continue;
				}

				if (c == '\n') {
					posy++;
					if (posy >= y) {
						for (int k = 1; k < y; k++)
							lines[k - 1] = lines[k];
						posy--;
						lines[y - 1] = new char[x];
						for (int k = 0; k < x; k++)
							lines[y - 1][k] = ' ';
					}
					continue;
				}

				if (c < 32) {
					continue;
				}

				if (posx >= x) {
					posx = 0;
					posy++;
					if (posy >= y)
					{
						posy--;
						for (int k = 1; k < y; k++)
							lines[k - 1] = lines[k];
						lines[y - 1] = new char[x];
						for (int k = 0; k < x; k++)
							lines[y - 1][k] = ' ';
					}
				}

				if (lines[posy] == null) {
					lines[posy] = new char[x];
					for (int k = 0; k < x; k++)
						lines[posy][k] = ' ';
				}

				lines[posy][posx] = c;
				posx++;
			}

			StringBuffer sb = new StringBuffer(x * y + y);

			for (int i = 0; i < lines.length; i++) {
				if (i != 0)
					sb.append('\n');

				if (lines[i] != null) {
					sb.append(lines[i]);
				}

			}
			setText(sb.toString());
		}

		public void run()
		{
			byte[] buff = new byte[8192];

			try {
				while (true) {
					int len = in.read(buff);
					if (len == -1)
						return;
					addText(buff, len);
				}
			} catch (Exception e) {
			}
		}
	}

	public TerminalArea(ConnectionThread con) throws IOException
	{
		this.x = 135;
		this.y = 300;
		this.conn = con;
		Session sess = conn.getSession();
		
		sess.requestPTY("dumb", this.x, this.y, 0, 0, null);
		sess.startShell();

		in = sess.getStdout();
		out = sess.getStdin();

		terminalOutput.setLineWrap(false);
		terminalOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
		terminalOutput.setBackground(Color.BLACK);
		terminalOutput.setForeground(Color.GREEN);
		terminalOutput.setCaretColor(Color.BLACK);
		terminalOutput.getCaret().setBlinkRate(0);
		terminalOutput.setEditable(false);

		terminalOutput.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				int c = e.getKeyChar();

				try {
					out.write(c);
				} catch (IOException e1) {
				}
				
				e.consume();
			}
		});

		terminalOutput.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					String selection = terminalOutput.getSelectedText();
					if (selection != null)
						textTransfer.setClipboardContents(selection);
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					try {
						conn.sendMessage(textTransfer.getClipboardContents());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					setCaretToEnd();
				}
			}
		});
		setLayout(new BorderLayout());
		add(new JScrollPane(terminalOutput), BorderLayout.CENTER);

		new RemoteConsumer().start();
	}
	
	public ConnectionThread getConnection() {
		return this.conn;
	}
	
	public void setCaretToEnd() {
		terminalOutput.setCaretPosition(terminalOutput.getText().trim().length());
	}
	
	public void setText(String text) {
		terminalOutput.setText(text);
		setCaretToEnd();
	}
	
	public void requestFocus() {
		terminalOutput.requestFocus();
	}
	
	protected void finalize() throws Throwable {
		conn.close();
	}
}
