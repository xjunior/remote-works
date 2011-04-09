package me.xjunior.remoteworks.gui;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FavoriteFormDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	JTextField name = new JTextField("", 20);
	JTextField hostname = new JTextField("", 20);
	JTextField username = new JTextField("", 20);
	private ActionListener listener;
	
	public FavoriteFormDialog(MainWindow parent) throws Exception {
		this(parent, null);
	}
	
	public FavoriteFormDialog(MainWindow parent, String entryName) throws Exception {
		super(parent, "Favorite");
		
		if (entryName != null) {
			String[] entry = me.xjunior.remoteworks.Main.favorites.getEntry(entryName);
			username.setText(entry[0]);
			hostname.setText(entry[1]);
			name.setEnabled(false);
		}
		
		JButton saveBtn = new JButton("Save");
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				me.xjunior.remoteworks.Main.favorites.addFavorite(name.getText(),
						username.getText(), hostname.getText());
				try {
					me.xjunior.remoteworks.Main.favorites.save();
					dispose();
					listener.actionPerformed(arg0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		JPanel panel = new JPanel();
		panel.add(new JLabel("Name: "));
		panel.add(name);
		panel.add(new JLabel("Hostname: "));
		panel.add(hostname);
		panel.add(new JLabel("Username: "));
		panel.add(username);
		panel.add(saveBtn);
		add(panel);
		setMinimumSize(new Dimension(700, 100));
		setSize(getMinimumSize());
	}

	public void setActionListener(ActionListener actionListener) {
		this.listener = actionListener;
	}
}
