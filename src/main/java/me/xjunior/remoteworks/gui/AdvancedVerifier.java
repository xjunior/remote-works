package me.xjunior.remoteworks.gui;

import java.io.IOException;

import javax.swing.JOptionPane;

import ch.ethz.ssh2.KnownHosts;
import ch.ethz.ssh2.ServerHostKeyVerifier;

/**
 * This ServerHostKeyVerifier asks the user on how to proceed if a key cannot be found
 * in the in-memory database.
 *
 */
class AdvancedVerifier implements ServerHostKeyVerifier
{
	public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm,
			byte[] serverHostKey) throws Exception {
		final String host = hostname;
		final String algo = serverHostKeyAlgorithm;

		String message;

		// Check database 

		int result = me.xjunior.remoteworks.Main.database.verifyHostkey(hostname, serverHostKeyAlgorithm, serverHostKey);

		switch (result) {
		case KnownHosts.HOSTKEY_IS_OK:
			return true;

		case KnownHosts.HOSTKEY_IS_NEW:
			message = "Do you want to accept the hostkey (type " + algo + ") from " + host + " ?\n";
			break;

		case KnownHosts.HOSTKEY_HAS_CHANGED:
			message = "WARNING! Hostkey for " + host + " has changed!\nAccept anyway?\n";
			break;

		default:
			throw new IllegalStateException();
		}

		// Include the fingerprints in the message

		String hexFingerprint = KnownHosts.createHexFingerprint(serverHostKeyAlgorithm, serverHostKey);
		String bubblebabbleFingerprint = KnownHosts.createBubblebabbleFingerprint(serverHostKeyAlgorithm,
				serverHostKey);

		message += "Hex Fingerprint: " + hexFingerprint + "\nBubblebabble Fingerprint: " + bubblebabbleFingerprint;

		// Now ask the user

		int choice = JOptionPane.showConfirmDialog(null, message);

		if (choice == JOptionPane.YES_OPTION) {
			// Be really paranoid. We use a hashed hostname entry

			String hashedHostname = KnownHosts.createHashedHostname(hostname);

			// Add the hostkey to the in-memory database

			me.xjunior.remoteworks.Main.database.addHostkey(new String[] { hashedHostname }, serverHostKeyAlgorithm, serverHostKey);

			// Also try to add the key to a known_host file

			try {
				KnownHosts.addHostkeyToFile(me.xjunior.remoteworks.Main.config.getKnownHostsFile(), new String[] { hashedHostname },
						serverHostKeyAlgorithm, serverHostKey);
			} catch (IOException ignore) {
			}

			return true;
		}

		if (choice == JOptionPane.CANCEL_OPTION) {
			throw new Exception("The user aborted the server hostkey verification.");
		}

		return false;
	}
}