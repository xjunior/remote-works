package me.xjunior.remoteworks.gui;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import me.xjunior.remoteworks.script.ScriptSource;

public class ScriptEditorDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	MainWindow window;
	JTextArea textarea = new JTextArea();
	
	public ScriptEditorDialog(MainWindow parent) {
		super(parent);
		setTitle("Script Editor");
		
		this.window = parent;
		
		setMinimumSize(new Dimension(400, 300));
		
		JButton runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					ScriptSource source = new ScriptSource(textarea.getText(), me.xjunior.remoteworks.Main.config.getScriptsPath());
					window.runSript(source);
					dispose();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		Container pane = this.getContentPane();
		pane.add(new JScrollPane(textarea), BorderLayout.CENTER);
		pane.add(runButton, BorderLayout.SOUTH);
	}
}
