package com.moosemanstudios.Birthdays.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class Birthdays extends JavaPlugin {
	public Logger log = Logger.getLogger("minecraft");
	private String prefix = "[Birthdays] ";
    public PluginDescriptionFile pdfFile = this.getDescription();
	private Boolean debug;
    public Boolean updaterEnabled, updaterAuto, updaterNotify;
    public static final int curseID = 0;

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
        getCommand("birthday").setExecutor(new BirthdaysCommandExecutor(this));

        // all done
        log.info(prefix + "Plugin enabled successfully");
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
    }

    public File getFileFolder() {
        return this.getFile();
    }
}