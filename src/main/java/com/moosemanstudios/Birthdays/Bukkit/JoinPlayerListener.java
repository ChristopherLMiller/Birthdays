package com.moosemanstudios.Birthdays.Bukkit;

import com.moosemanstudios.Birthdays.Core.BirthdayManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinPlayerListener implements Listener {
	Birthdays plugin;
	
	JoinPlayerListener(Birthdays plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		// see if they are even in the configuration first
		if (!BirthdayManager.getInstance().getConfig().contains(player.getName() + ".birthday") && (BirthdayManager.getInstance().getConfig().getInt(player.getName() + ".informed") <= plugin.maxNotify - 1)) {
			player.sendMessage(ChatColor.AQUA + "Want to receive gifts on your birthday?");
			player.sendMessage(ChatColor.AQUA + "Type" + ChatColor.RESET + " /birthdays set <MM/DD>" + ChatColor.AQUA + " to set your birthday now!");

			// increment the number of times we have informed user
			BirthdayManager.getInstance().getConfig().set(player.getName() + ".informed", BirthdayManager.getInstance().getConfig().getInt(player.getName() + ".informed") + 1);
			BirthdayManager.getInstance().saveConfig();
		}
    }
}
