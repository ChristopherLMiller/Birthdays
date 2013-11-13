package com.moosemanstudios.Birthdays.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class Birthdays extends JavaPlugin {
	public Logger log = Logger.getLogger("minecraft");
	private String prefix = "[Birthdays] ";
	private Boolean debug;
	public Boolean updaterEnabled, updaterAuto, updaterNotify;
	public int curseID = 0;
	
	@Override
	public void onEnable() {
		
		// load the config
		loadConfig();
		
		// check updater settings
		if (updaterEnabled) {
			if (updaterAuto) {
				Updater updater = new Updater(this, curseID, this.getFile(), Updater.UpdateType.DEFAULT, true);
				if (updater.getResult() == Updater.UpdateResult.SUCCESS)
					log.info(prefix + " Update downloaded successfully, restart server to apply update");
			}
			if (updaterNotify) {
				log.info(prefix + "Notifying admins as they login if update found");
				this.getServer().getPluginManager().registerEvents(new  UpdaterPlayerListener(this), this);
			}
			
		}
		
		// register the command listener
		getCommand("birthday").setExecutor(new BirthdaysCommandExecutor(this));
		
		// lastly register the metrics listener
		try {
			Metrics metrics = new MetricsBukkit(this.getName(), this.getDescription().getVersion());
			metrics.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadConfig() {
		// misc
		if (!getConfig().contains("debug")) getConfig().set("debug", false);
		
		// updater
		if (!getConfig().contains("updater.enabled")) getConfig().set("updater.enabled", true);
		if (!getConfig().contains("updater.auto")) getConfig().set("updater.auto", true);
		if (!getConfig().contains("updater.notify")) getConfig().set("updater.notify", true);
		
		saveConfig();
		
		debug = getConfig().getBoolean("debug");
		if (debug) {
			log.info(prefix + "Debugging enabled");
		}
		
		updaterEnabled = getConfig().getBoolean("updater.enabled");
		updaterAuto = getConfig().getBoolean("updater.auto");
		updaterNotify = getConfig().getBoolean("updater.notify");
		if (debug) {
			if (updaterEnabled)
				log.info(prefix + "Updater enabled");
			if (updaterAuto)
				log.info(prefix + "Auto updating enabled");
			if (updaterNotify)
				log.info(prefix + "Notfying admins of updates at login");
		}	
	}
	
	public File getFileFolder() {
		return this.getFile();
	}
	
}
