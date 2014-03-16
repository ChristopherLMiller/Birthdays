package com.moosemanstudios.Birthdays.Core;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BirthdayManager {
	private static BirthdayManager instance = null;
	private YamlConfiguration config;
	private File file;

	/**
	 * Default constructor - only exists to defeat default instantiation
	 */
	BirthdayManager() {}

	/**
	 * Get the instance of the manager
	 * @return instance
	 */
	public static BirthdayManager getInstance() {
		if (instance == null) {
			instance = new BirthdayManager();
		}
		return instance;
	}

	/**
	 * @param	filename	the filename to open
	 * @return			if file loaded successfully
	 */
	public Boolean load(String filename) throws IOException, InvalidConfigurationException {
		if (file == null)
			file = new File(filename);

		config = new YamlConfiguration();

		// try and load the config
		try {
			config.load(file);
		} catch (FileNotFoundException e) {
			// file doesn't exist, create it
			file.createNewFile();
			return true;
		}

		return true;
	}

	public YamlConfiguration getConfig() {
		return this.config;
	}

	public void saveConfig() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reloadConfig() {
		try {
			config.load(file);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
}
