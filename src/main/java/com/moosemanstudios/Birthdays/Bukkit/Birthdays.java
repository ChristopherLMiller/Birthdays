package com.moosemanstudios.Birthdays.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;

import org.mcstats.Metrics;

import net.milkbowl.vault.economy.Economy;

public class Birthdays extends JavaPlugin {
	public Logger log = Logger.getLogger("minecraft");
	private String prefix = "[Birthdays] ";
    public PluginDescriptionFile pdfFile = this.getDescription();
	private Boolean debug;
    public Boolean updaterEnabled, updaterAuto, updaterNotify;
    public static final int curseID = 0;

    public Boolean itemGiftEnabled, currencyGiftEnabled;
    public int itemGiftAmount, currencyGiftAmount;
    public Material itemGiftType;

	public static Economy economy = null;

	@Override
	public void onEnable() {
        // load the config
        loadConfig();

        // enable metrics tracking
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // check updater settings - Note values obtained from config
        if (updaterEnabled) {
            if (updaterAuto) {
                Updater updater = new Updater(this, curseID, this.getFile(), Updater.UpdateType.DEFAULT, true);
                if (updater.getResult() == Updater.UpdateResult.SUCCESS)
                    log.info(prefix + "Update downloaded successfully, restart server to apply update");
            }
            if (updaterNotify) {
                this.getServer().getPluginManager().registerEvents(new UpdaterPlayerListener(this), this);
            }
        }

        // register the command executor
        getCommand("birthdays").setExecutor(new BirthdaysCommandExecutor(this));

        // check for vault if currency is requested
        if (currencyGiftEnabled) {
			if (!setupEconomy()) {
				log.warning(prefix + "Vault and/or a economy plugin were not found.  Turning off currency gifts.");
				currencyGiftEnabled = false;
			}
		}

        // check that we still have one method enabled, if not lets disable the plugin
        if (!itemGiftEnabled && !currencyGiftEnabled) {
            log.warning(prefix + "At least one gift type must be enabled, disabling plugin");
            Bukkit.getPluginManager().disablePlugin(this);
        }

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

        // updater settings
        if (!getConfig().contains("updater.enabled")) getConfig().set("updater.enabled", true);
        if (!getConfig().contains("updater.auto")) getConfig().set("updater.auto", true);
        if (!getConfig().contains("updater.notify")) getConfig().set("updater.notify", true);

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

        updaterEnabled = getConfig().getBoolean("updater.enabled");
        updaterAuto = getConfig().getBoolean("updater.auto");
        updaterNotify = getConfig().getBoolean("updater.notify");
        if (debug && updaterEnabled) {
            if (updaterAuto)
                log.info(prefix + "Auto updating enabled");
            if (updaterNotify)
                log.info(prefix + "Notifying admins on login of updates");
        }

        itemGiftEnabled = getConfig().getBoolean("gifts.item.enabled");
        itemGiftType = Material.getMaterial(getConfig().getString("gifts.item.type"));
        itemGiftAmount = getConfig().getInt("gifts.item.amount");
        currencyGiftEnabled = getConfig().getBoolean("gifts.currency.enabled");
        currencyGiftAmount = getConfig().getInt("gifts.currency.amount");

        if (debug && itemGiftEnabled) {
            log.info(prefix + "Giving out " + /*Integer.toString(itemGiftAmount) + " of " +*/ itemGiftType);
        }
        if (debug && currencyGiftEnabled) {
            log.info(prefix + "Giving " + Integer.toString(currencyGiftAmount) + " money out for gifts");
        }
    }

    public File getFileFolder() {
        return this.getFile();
    }
}