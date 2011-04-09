package me.xjunior.remoteworks;


import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import me.xjunior.remoteworks.gui.MainWindow;
import ch.ethz.ssh2.KnownHosts;

public class Main {
	public static final KnownHosts database = new KnownHosts();
	public static Config config;
	public static Favorites favorites;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			new File(Config.getDefaultAppDir()).mkdirs(); // create config dir if doesn't exist yet
			
			config = new Config(new File(Config.getDefaultAppDir() + File.separator + "remoteworks.config"));
			favorites = new Favorites(config.getFavoritesFile());
			
			File knownHostFile = config.getKnownHostsFile();
			if (knownHostFile.exists())
				database.addHostkeys(knownHostFile);
			
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	    			MainWindow main = new MainWindow("Remote Works [ by xjunior.me ]");
	    			ImageIcon img = new ImageIcon(getClass().getResource("/me/xjunior/remoteworks/gfx/icon.png"));
	    			main.setIconImage(img.getImage());
	    			main.pack();
	    	        main.setVisible(true);
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
