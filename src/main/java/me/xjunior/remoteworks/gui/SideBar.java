package me.xjunior.remoteworks.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

public class SideBar extends JPanel {
	private static final long serialVersionUID = 1L;
	JToolBar toolbar = new JToolBar();
	
	public SideBar() {
		setLayout(new BorderLayout());

		toolbar.setFloatable(false);
		add(toolbar, BorderLayout.NORTH);
	}
	
	public JToolBar getToolbar() {
		return toolbar;
	}
	
	public void setComponent(Component c) {
		add(new JScrollPane(c), BorderLayout.CENTER);
	}
}
