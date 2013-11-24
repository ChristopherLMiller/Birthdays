package com.moosemanstudios.Birthdays.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import net.gravitydevelopment.updater.Updater;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class Birthdays extends JavaPlugin {
	public Logger log = Logger.getLogger("minecraft");
	private String prefix = "[Birthdays] ";
	private Boolean debug;
	public Boolean updaterEnabled, updaterAuto, updaterNotify;
	public int curseID = 0;
	public Boolean itemEnabled, economyEnabled;
	public String itemName;
	public int itemAmount, economyAmount;
	public static Economy economy = null;
	
	@Override
	public void onEnable() {
		// load the config
		loadConfig();
		
		// check updater settings
		if (updaterEnabled) {
			if (updaterAuto) {
				Updater updater = new Updater(this, curseID, this.getFile(), Updater.UpdateType.DEFAULT, true);
				if (updater.getResult() == Updater.UpdateResult.SUCCESS)
					log.info(prefix + "Update downloaded successfully, restart server to apply update");
			}
			if (updaterNotify) {
				log.info(prefix + "Notifying admins as they login if update found");
				this.getServer().getPluginManager().registerEvents(new  UpdaterPlayerListener(this), this);
			}
			
		}
		
		// check if vault is found
		if (economyEnabled) {
			if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
				log.warning(prefix + "Vault was not found on startup.  Economy mode not enabled");
			} else {
				// attempt to get a economy plugin
				RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
				if (economyProvider != null) {
					economy = economyProvider.getProvider();
				} else {
					economyEnabled = false;
					log.warning(prefix + "No economy plugins found. disabling economoy mode");
				}
			}
		}
		
		// validate the item name
		if (Material.getMaterial(itemName.toUpperCase()) == null) {
			log.warning(prefix + "Invalid item name: " + itemName + " Please check spelling of item, disabling item mode");
			itemEnabled = false;
		}
		
		// make sure one of the two is enabled, if not well lets disable the plugin
		if (!itemEnabled && !economyEnabled) {
			log.warning(prefix + "Neither mode is enabled, disabling plugin");
			getServer().getPluginManager().disablePlugin(this);
		}
				
		// register the player listener
		this.getServer().getPluginManager().registerEvents(new JoinPlayerListener(this), this);
		
		
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
		
		// reward methods
		if (!getConfig().contains("item.enabled")) getConfig().set("item.enabled", true);
		if (!getConfig().contains("item.itemName")) getConfig().set("item.itemName", "diamond");
		if (!getConfig().contains("item.amount")) getConfig().set("item.amount", 1);
		
		// economy rewards
		if (!getConfig().contains("economy.enabled")) getConfig().set("economy.enabled", true);
		if (!getConfig().contains("economy.amount")) getConfig().set("economy.amount", 100);
		
		
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
		
		itemEnabled = getConfig().getBoolean("item.enabled");
		itemName = getConfig().getString("item.itemName");
		itemAmount = getConfig().getInt("item.amount");
		
		if (debug && itemEnabled) {
				log.info(prefix + "Item gifts enabled, Item Type: "  + itemName +  " Item Amount: " + itemAmount);
		}
		
		economyEnabled = getConfig().getBoolean("economy.enabled");
		economyAmount = getConfig().getInt("economy.amount");
		
		if (economyEnabled && debug) {
			log.info(prefix + "Economy gifts enabled - Amount to give: " + economyAmount);
		}
	}
	
	public File getFileFolder() {
		return this.getFile();
	}
	
	public Boolean giveGifts(String player) {
		// first, lets wish the person a happy birthday
		String message = "HAPPY BIRTHDAY!";
		String finalMessage = "";
		String[] colors = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
		for(int i = 0; i < message.length(); i++) {
			Random rand = new Random();
			finalMessage = finalMessage + "&" + colors[rand.nextInt(colors.length)] + message.charAt(i);
		
		}
		this.getServer().getPlayer(player).sendMessage(ChatColor.translateAlternateColorCodes('&', finalMessage));		
		return false;
		
	}
	
}
