package me.xjunior.remoteworks.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import me.xjunior.remoteworks.script.ScriptSource;

public class ScriptTree extends JTree {
	private static final long serialVersionUID = 1L;
	
	private DefaultMutableTreeNode top;
	
	File path;
	
	ActionListener listener;

	public ScriptTree(File path) {
		super(new DefaultMutableTreeNode("Available Scripts"));
		top = (DefaultMutableTreeNode) getModel().getRoot();
		
		initializeTree();
		setPath(path);
		expandRow(0);
	}
	
	public void setPath(File path) {
		this.path = path;
		refresh();
	}
	
	public File getPath() {
		return path;
	}
	
	private void initializeTree() {
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2 && getSelectedScript() != null && listener != null)
					listener.actionPerformed(new ActionEvent(this, e.getID(), "script"));
			}
		});
	}

	public void refresh() {
		top.removeAllChildren();
		try {
			fillTree(top, new File(path.getAbsolutePath()));
		} catch (IOException e) {
			top.add(new DefaultMutableTreeNode("-- Failed to initialize scripts --"));
		}
		((DefaultTreeModel)getModel()).reload();
	}

	private void fillTree(DefaultMutableTreeNode parent, File dir) throws IOException {
		if (dir.exists() && dir.isDirectory()) {
			for (File cur : dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return !name.startsWith(".") && (name.endsWith(".sh") || new File(dir.getAbsolutePath(), name).isDirectory());
				}
			})) {
				DefaultMutableTreeNode node = null;
				if (cur.isDirectory()) {
					node = new DefaultMutableTreeNode(cur.getName());
					fillTree(node, cur);
					if (node.getChildCount() == 0) {
						node.add(new DefaultMutableTreeNode("-- Empty --"));
					}
				} else
					node = new DefaultMutableTreeNode(new ScriptSource(cur));
				
				parent.add(node);
			}
		} else throw new FileNotFoundException();
	}
	
	public ScriptSource getSelectedScript() {
		if (getComponentCount() == 0) return null;
		DefaultMutableTreeNode obj =
			(DefaultMutableTreeNode)getLastSelectedPathComponent();
		if (obj == null || !obj.isLeaf() || obj.isRoot()) return null;
		return (obj.getUserObject() instanceof ScriptSource) ?
				(ScriptSource)obj.getUserObject() : null;
	}
	
	public void setActionListener(ActionListener listener) {
		this.listener = listener;
	}
}
