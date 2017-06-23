package com.moosemanstudios.Birthdays.Bukkit;

import com.moosemanstudios.Birthdays.Core.BirthdayManager;
import net.milkbowl.vault.economy.Economy;
import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Birthdays extends JavaPlugin {
	public Logger log = Logger.getLogger("minecraft");
	public String prefix = "[Birthdays] ";
    public PluginDescriptionFile pdfFile = this.getDescription();
	private Boolean debug;
    public Boolean updaterEnabled, updaterAuto, updaterNotify;
    public static final int curseID = 68957;

	public Boolean broadcastOnJoin;

    public Boolean itemGiftEnabled, currencyGiftEnabled;
    public int itemGiftAmount, currencyGiftAmount;
    public Material itemGiftType;

	public static Economy economy = null;

	public int maxNotify;

	@Override
	public void onEnable() {
        // load the config
        loadConfig();

        // enable metrics tracking
        Metrics metrics = new Metrics(this);

        // register the command executor
        getCommand("birthdays").setExecutor(new BirthdaysCommandExecutor(this));

        // check for vault if currency is requested
        if (currencyGiftEnabled) {
			if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
				if (!setupEconomy()) {
					log.warning(prefix + "No economy plugin was found.  Turning off currency gifts.");
					currencyGiftEnabled = false;
				}
			} else {
				log.warning(prefix + "Vault was not found and is required for currency gifts. Disabling currency");
				currencyGiftEnabled = false;
			}
		}

        // check that we still have one method enabled, if not lets disable the plugin
        if (!itemGiftEnabled && !currencyGiftEnabled) {
            log.warning(prefix + "At least one gift type must be enabled, disabling plugin");
            Bukkit.getPluginManager().disablePlugin(this);
        }

		// Initialize the birthday manager
		try {
			BirthdayManager.getInstance().load(getDataFolder() + File.separator + "players.yml");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		// register the event listener
		Bukkit.getPluginManager().registerEvents(new JoinPlayerListener(this), this);

        // all done
        log.info(prefix + "Plugin enabled successfully");
    }

	public Boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}
    public void loadConfig() {
        // start by reloading the config
        reloadConfig();

        // misc settings
        if (!getConfig().contains("misc.debug")) getConfig().set("misc.debug", true);
		if (!getConfig().contains("misc.max-notifications")) getConfig().set("misc.max-notifications", 3);
		if (!getConfig().contains("misc.broadcast-on-join")) getConfig().set("misc.broadcast-on-join", true);

        // gifts
        if (!getConfig().contains("gifts.item.enabled")) getConfig().set("gifts.item.enabled", true);
        if (!getConfig().contains("gifts.item.type")) getConfig().set("gifts.item.type", "diamond");
        if (!getConfig().contains("gifts.item.amount")) getConfig().set("gifts.item.amount", 1);
        if (!getConfig().contains("gifts.currency.enabled")) getConfig().set("gifts.currency.enabled", true);
        if (!getConfig().contains("gifts.currency.amount")) getConfig().set("gifts.currency.amount", 50);

        // save the config before reading back
        saveConfig();

        debug = getConfig().getBoolean("misc.debug");
        if (debug)
            log.info(prefix + "Debugging enabled");

		maxNotify = getConfig().getInt("misc.max-notifications");
		if (debug)
			log.info(prefix + "Max notifications on join: " + maxNotify);

		broadcastOnJoin = getConfig().getBoolean("misc.broadcast-on-join");

        itemGiftEnabled = getConfig().getBoolean("gifts.item.enabled");
        itemGiftType = Material.matchMaterial(getConfig().getString("gifts.item.type"));
        itemGiftAmount = getConfig().getInt("gifts.item.amount");
        currencyGiftEnabled = getConfig().getBoolean("gifts.currency.enabled");
        currencyGiftAmount = getConfig().getInt("gifts.currency.amount");

        if (debug && itemGiftEnabled) {
            log.info(prefix + "Giving out " + Integer.toString(itemGiftAmount) + " of " + itemGiftType);
        }
        if (debug && currencyGiftEnabled) {
            log.info(prefix + "Giving " + Integer.toString(currencyGiftAmount) + " money out for gifts");
        }
    }

    public File getFileFolder() {
        return this.getFile();
    }
}