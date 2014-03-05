package com.moosemanstudios.Birthdays.Bukkit;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BirthdaysCommandExecutor implements CommandExecutor {

	private Birthdays plugin;
	private CommandSender sender;
	
	public BirthdaysCommandExecutor(Birthdays plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("birthday") || cmd.getName().equalsIgnoreCase("bdays")) {
            if (args.length == 0) {
                displayHelp(sender);
            } else {
                if (args[0].equalsIgnoreCase("help"))
                    displayHelp(sender);
                else if (args[0].equalsIgnoreCase("version"))
                    showVersion(sender);
                else if (args[0].equalsIgnoreCase("update"))
                    update(sender);
                else
                    sender.sendMessage(ChatColor.WHITE + "Invalid command.  Please see " + ChatColor.RED + "/birthdays help" + ChatColor.WHITE + " for all available commands");
            }
            return true;
        }
		return false;
	}

    public void displayHelp(CommandSender sender) {
        sender.sendMessage("Birthdays - Command Helper");
        sender.sendMessage("------------------------------");
        sender.sendMessage("/birthdays help" + ChatColor.RED + ": Display this screen");
        sender.sendMessage("/birthdays version" + ChatColor.RED + ": Show plugin version");

        if (sender.hasPermission("birthdays.update")) {
            sender.sendMessage("/birthdays update" + ChatColor.RED + ": Check for and apply update");
        }
    }

    public void showVersion(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Birthdays Version:" + ChatColor.WHITE + plugin.pdfFile.getVersion() + ChatColor.GOLD + " Author: moose517");
    }

    public void update(CommandSender sender) {
        if (sender.hasPermission("birthdays.update")) {
            if (plugin.updaterEnabled) {
                Updater updater = new Updater(plugin, plugin.curseID, plugin.getFileFolder(), Updater.UpdateType.NO_DOWNLOAD, false);
                if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
                    sender.sendMessage(ChatColor.AQUA + "Update found, starting download: " + updater.getLatestName());
                    updater = new Updater(plugin, plugin.curseID, plugin.getFileFolder(), Updater.UpdateType.DEFAULT, true);

                    if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
                        sender.sendMessage(ChatColor.AQUA + "Update downloaded successfully, restart server to apply update");
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "Update download failed.  Please try again later or contact moose517");
                    }
                } else  if (updater.getResult() == Updater.UpdateResult.NO_UPDATE) {
                    sender.sendMessage(ChatColor.AQUA + "No update found");
                } else {
                    sender.sendMessage(ChatColor.AQUA + "Something has gone wrong. Contact moose517 on bukkit.org");
                }

            }
        } else {
            sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "birthdays.update");
        }
    }
}