package com.moosemanstudios.Birthdays.Bukkit;

import com.moosemanstudios.Birthdays.Core.BirthdayManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
		} else {
			if (BirthdayManager.getInstance().getConfig().contains(player.getName() + ".birthday")) {
				// see if its the players birthday
				String birthday = BirthdayManager.getInstance().getConfig().getString(player.getName() + ".birthday");
				int claimedYear = BirthdayManager.getInstance().getConfig().getInt(player.getName() + ".claimed");

				// get and format the current date to match what we store
				String date = new SimpleDateFormat("MM/dd").format(Calendar.getInstance().getTime());
				int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));

				if ((claimedYear < year) && (birthday.equals(date))) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9* &a* &b* &c* &d* &e* &f* &9* &a* &b* &c* &d* &e* &f* &9* &a* &b* &c* &d* &e* &f* &9* &a* &b* &c* &d* &e* &f*"));
					player.sendMessage(ChatColor.AQUA + "HAPPY BIRTHDAY!");
					player.sendMessage(ChatColor.AQUA + "To get your birthday present, type " + ChatColor.WHITE + "/birthdays claim");

					if (plugin.broadcastOnJoin) {
						Bukkit.broadcastMessage(ChatColor.AQUA + "Today is " + player.getName() + "'s Birthday!");
					}

					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9* &a* &b* &c* &d* &e* &f* &9* &a* &b* &c* &d* &e* &f* &9* &a* &b* &c* &d* &e* &f* &9* &a* &b* &c* &d* &e* &f*"));
				}
			}
		}
    }
}
