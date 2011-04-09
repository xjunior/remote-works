package me.xjunior.remoteworks.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class FavoritesSideBar extends SideBar {
	private static final long serialVersionUID = 1L;
	
	MainWindow main;
	JTree tree;
	DefaultMutableTreeNode root;
	
	public FavoritesSideBar(MainWindow main) {
		super();
		this.main = main;
		
		initializeTree();
		initializeToolbar();
	}

	private void initializeToolbar() {
		JButton addBtn = new JButton(new ImageIcon(getClass().getResource("/me/xjunior/remoteworks/gfx/add_icon.gif")));
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					FavoriteFormDialog dialog = new FavoriteFormDialog(main);
					dialog.setVisible(true);
					dialog.setActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							reloadTree();
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		JButton removeBtn = new JButton(new ImageIcon(getClass().getResource("/me/xjunior/remoteworks/gfx/remove_icon.gif")));
		removeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FavoriteTreeEntry fav = getSelectedFavorite();
				if (fav != null) {
					int response = JOptionPane.showConfirmDialog(main, "Remove " + fav + " from favorites?");
					if (response == 0) {
						try {
								me.xjunior.remoteworks.Main.favorites.removeFavorite(fav.toString());
								me.xjunior.remoteworks.Main.favorites.save();
								reloadTree();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
		getToolbar().add(addBtn);
		getToolbar().add(removeBtn);
	}

	private void initializeTree() {
		root = new DefaultMutableTreeNode("Favorites");
		tree = new JTree(root);
		reloadTree();
		tree.expandRow(0);
		
		tree.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					FavoriteTreeEntry entry = getSelectedFavorite();
					if (entry != null) {
						String[] split = entry.getEntry();
						main.createConnection(split[0], split[1], entry.toString());
					}
				}
			}
		});
		
		setComponent(tree);
	}
	
	private FavoriteTreeEntry getSelectedFavorite() {
		DefaultMutableTreeNode obj = (DefaultMutableTreeNode)
			tree.getLastSelectedPathComponent();
		if (obj == null || !obj.isLeaf() || obj.isRoot()) return null;
		return (obj.getUserObject() instanceof FavoriteTreeEntry) ? 
				(FavoriteTreeEntry)obj.getUserObject() : null;
	}
	
	private void reloadTree() {
		root.removeAllChildren();
		try {
			for (String key : me.xjunior.remoteworks.Main.favorites.getList()) {
				String[] entry = me.xjunior.remoteworks.Main.favorites.getEntry(key);
				root.add(new DefaultMutableTreeNode(new FavoriteTreeEntry(key, entry)));
			}
		} catch (Exception e) {
			root.add(new DefaultMutableTreeNode(e.getMessage()));
		}
		((DefaultTreeModel)tree.getModel()).reload();
	}
	
	private class FavoriteTreeEntry {
		String name;
		String[] entry;
		
		FavoriteTreeEntry(String name, String[] entry) {
			this.name = name;
			this.entry = entry;
		}
		
		public String[] getEntry() {
			return entry;
		}
		
		public String toString() {
			return name;
		}
	}
}
