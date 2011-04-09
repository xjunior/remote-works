package me.xjunior.remoteworks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class Favorites {
	Properties favs;
	File file;
	
	public Favorites(File file) throws IOException {
		favs = new Properties();
		try {
			favs.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// does nothing
		}
		this.file = file;
	}
	
	public void addFavorite(String key, String username, String hostname) {
		favs.setProperty(key, username + "@" + hostname);
	}
	
	public void save() throws FileNotFoundException, IOException {
		favs.store(new FileOutputStream(file), null);
	}
	
	public Set<String> getList() {
		return favs.stringPropertyNames();
	}
	
	public String[] getEntry(String key) throws Exception {
		String entry = favs.getProperty(key);
		if (entry == null)
			throw new Exception("Entry not found: " + key);
		String[] split = entry.split("@"); 
		if (split.length != 2)
			throw new Exception("Error parsing favorites entry: " + entry);
		return split;
	}

	public void removeFavorite(String string) {
		favs.remove(string);
	}
}
