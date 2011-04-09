package me.xjunior.remoteworks.gui;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * This dialog displays a number of text lines and a text field.
 * The text field can either be plain text or a password field.
 */
public class EnterSomethingDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	JTextField answerField;
	JPasswordField passwordField;

	final boolean isPassword;

	String answer;

	public EnterSomethingDialog(JFrame parent, String title, String content, boolean isPassword)
	{
		this(parent, title, new String[] { content }, isPassword);
	}

	public EnterSomethingDialog(JFrame parent, String title, String[] content, boolean isPassword)
	{
		super(parent, title, true);

		this.isPassword = isPassword;

		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));

		for (int i = 0; i < content.length; i++)
		{
			if ((content[i] == null) || (content[i] == ""))
				continue;
			JLabel contentLabel = new JLabel(content[i]);
			pan.add(contentLabel);

		}

		answerField = new JTextField(20);
		passwordField = new JPasswordField(20);

		if (isPassword)
			pan.add(passwordField);
		else
			pan.add(answerField);

		KeyAdapter kl = new KeyAdapter()
		{
			public void keyTyped(KeyEvent e)
			{
				if (e.getKeyChar() == '\n')
					finish();
			}
		};

		answerField.addKeyListener(kl);
		passwordField.addKeyListener(kl);

		getContentPane().add(BorderLayout.CENTER, pan);

		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void finish()
	{
		if (isPassword)
			answer = new String(passwordField.getPassword());
		else
			answer = answerField.getText();

		dispose();
	}
}