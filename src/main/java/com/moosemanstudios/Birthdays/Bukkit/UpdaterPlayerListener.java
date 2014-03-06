package com.moosemanstudios.Birthdays.Bukkit;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdaterPlayerListener implements Listener {
	private Birthdays plugin;
	
	UpdaterPlayerListener(Birthdays plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
        // Note - this listener is never registered if the updater is disabled
		Player player = event.getPlayer();
		
		if (player.hasPermission("birthdays.admin")) {
			Updater updater = new Updater(plugin, plugin.curseID, plugin.getFileFolder(), Updater.UpdateType.NO_DOWNLOAD, false);
			
			if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE && plugin.updaterNotify && plugin.updaterEnabled) {
				player.sendMessage(ChatColor.AQUA + "An update is available for Birthdays: " + updater.getLatestName());
				player.sendMessage(ChatColor.AQUA + "Type " + ChatColor.WHITE + "/birthdays update " + ChatColor.AQUA + " to update");
			}
		}				
	}
}
