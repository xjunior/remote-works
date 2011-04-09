package me.xjunior.remoteworks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	Properties props;
	File configPath;
	
	public Config(File file) throws IOException {
		configPath = file;
		props = new Properties();
		try {
			props.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// does nothing
		}
	}
	
	public File getKnownHostsFile() {
		return new File(props.getProperty("ssh.known_hosts",
				System.getProperty("user.home") + File.separator + "known_hosts"));
	}
	
	public File getIdDsaFile() {
		return new File(props.getProperty("ssh.id_dsa", getDefaultAppDir() + File.separator + "id_dsa"));
	}
	
	public File getIdRsaFile() {
		return new File(props.getProperty("ssh.id_rsa", getDefaultAppDir() + File.separator + "id_rsa"));
	}
	
	public File getScriptsPath() {
		return new File(props.getProperty("scripts.path", System.getProperty("user.home") + File.separator + "scripts"));
	}
	
	public void setScriptsPath(File path) {
		props.setProperty("scripts.path", path.getAbsolutePath());
	}
	
	public File getFavoritesFile() {
		return new File(props.getProperty("favorites", getDefaultAppDir() + File.separator + "favorites"));
	}
	
	public static String getDefaultAppDir() {
		return System.getenv("APPDATA") + File.separator + "remoteworks"; 
	}
	
	public void save() throws FileNotFoundException, IOException {
		props.store(new FileOutputStream(configPath), null);
	}
}
