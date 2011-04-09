package me.xjunior.remoteworks.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import me.xjunior.remoteworks.script.ScriptSource;

public class ScriptsSideBar extends SideBar {
	private static final long serialVersionUID = 1L;
	ScriptTree tree;
	MainWindow main;
	
	public ScriptsSideBar(MainWindow m) {
		super();
		this.main = m;
		
		initializeTree();
		initializeToolbar();
	}

	private void initializeToolbar() {
		JButton scriptEditorBtn = new JButton("Script Editor");
		JButton reloadScritpsBtn = new JButton(new ImageIcon(getClass().getResource("/me/xjunior/remoteworks/gfx/reload_icon.gif")));
		reloadScritpsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tree.refresh();
			}
		});
		JButton openFolderBtn = new JButton(new ImageIcon(getClass().getResource("/me/xjunior/remoteworks/gfx/open_folder_icon.gif")));
		openFolderBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(tree.getPath());
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showOpenDialog(main) == JFileChooser.APPROVE_OPTION) {
					tree.setPath(fc.getSelectedFile());
					me.xjunior.remoteworks.Main.config.setScriptsPath(fc.getSelectedFile());
					try {
						me.xjunior.remoteworks.Main.config.save();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		getToolbar().add(openFolderBtn);
		getToolbar().add(reloadScritpsBtn);
		getToolbar().add(scriptEditorBtn);
		scriptEditorBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ScriptEditorDialog dlg = new ScriptEditorDialog(main);
				dlg.setVisible(true);
			}
		});
	}

	private void initializeTree() {
		tree = new ScriptTree(me.xjunior.remoteworks.Main.config.getScriptsPath());
		tree.setActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScriptSource source = tree.getSelectedScript();
				if (source != null)
					try {
						main.runSript(source);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
		});
		setComponent(tree);
	}
}
