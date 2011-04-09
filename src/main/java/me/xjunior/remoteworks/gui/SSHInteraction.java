package me.xjunior.remoteworks.gui;

import java.io.IOException;

import ch.ethz.ssh2.InteractiveCallback;

public class SSHInteraction implements InteractiveCallback {
	int promptCount = 0;
	String lastError;
	KeyboardInteractioListener listener;

	public SSHInteraction(String lastError)
	{
		this.lastError = lastError;
	}

	/* the callback may be invoked several times, depending on how many questions-sets the server sends */

	public String[] replyToChallenge(String name, String instruction, int numPrompts, String[] prompt,
			boolean[] echo) throws IOException
	{
		String[] result = new String[numPrompts];

		for (int i = 0; i < numPrompts; i++)
		{
			/* Often, servers just send empty strings for "name" and "instruction" */
			
			result[i] = listener.onInteraction(lastError, name, instruction, prompt[i], echo[i]);

			if (lastError != null) lastError = null; // show lastError only once

			/*if (result[i] == null)
				throw new IOException("Login aborted by user");*/

			promptCount++;
		}

		return result;
	}

	/* We maintain a prompt counter - this enables the detection of situations where the ssh
	 * server is signaling "authentication failed" even though it did not send a single prompt.
	 */
	
	public void setKeyboardInteractionListener(KeyboardInteractioListener listener) {
		this.listener =  listener;
	}

	public int getPromptCount()
	{
		return promptCount;
	}
}
