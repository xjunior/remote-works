package me.xjunior.remoteworks.gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginComponent extends JPanel {
	private static final long serialVersionUID = -3977552756932204196L;
	JTextField userField;
	JTextField hostField;
	JButton loginButton;
	
	public LoginComponent() {
		JLabel hostLabel = new JLabel("Hostname:");
		JLabel userLabel = new JLabel("Username:");

		hostField = new JTextField("", 20);
		userField = new JTextField("", 10);

		loginButton = new JButton("Login");

		JPanel loginPanel = new JPanel();

		loginPanel.add(hostLabel);
		loginPanel.add(hostField);
		loginPanel.add(userLabel);
		loginPanel.add(userField);
		loginPanel.add(loginButton);

		add(loginPanel, BorderLayout.PAGE_START);
	}
	
	public JTextField getUserField() {
		return userField;
	}
	
	public JTextField getHostField() {
		return hostField;
	}
	
	public JButton getLoginButton() {
		return loginButton;
	}
	
	public void setEnabled(boolean en) {
		this.userField.setEnabled(en);
		this.hostField.setEnabled(en);
		this.loginButton.setEnabled(en);
	}
}
