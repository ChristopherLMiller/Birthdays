package com.moosemanstudios.Birthdays.Bukkit;

import com.moosemanstudios.Birthdays.Core.BirthdayManager;
import net.gravitydevelopment.updater.Updater;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BirthdaysCommandExecutor implements CommandExecutor {

	private Birthdays plugin;
	private CommandSender sender;
	
	public BirthdaysCommandExecutor(Birthdays plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("birthdays") || cmd.getName().equalsIgnoreCase("bdays")) {
            if (args.length == 0) {
                displayHelp(sender);
            } else {
                if (args[0].equalsIgnoreCase("help"))
                    displayHelp(sender);
                else if (args[0].equalsIgnoreCase("version"))
                    showVersion(sender);
                else if (args[0].equalsIgnoreCase("update"))
                    update(sender);
				else if (args[0].equalsIgnoreCase("reload"))
					reload(sender, args);
				else if (args[0].equalsIgnoreCase("set"))
					setBirthday(sender, args);
                else
                    sender.sendMessage("Invalid command.  Please see " + ChatColor.RED + "/birthdays help" + ChatColor.WHITE + " for all available commands");
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
		if (sender.hasPermission("birthdays.reload.all")) {
			sender.sendMessage("/birthdays reload" + ChatColor.RED + ": Reload all configuration files");
		}
		if (sender.hasPermission("birthdays.reload.config")) {
			sender.sendMessage("/birthdays reload config" + ChatColor.RED + ": Reload the config from disk");
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

	public void reload(CommandSender sender, String args[]) {
		if (args.length == 1) {
			if (sender.hasPermission("birthdays.reload.all")) {
				// TODO: reload all configs
				plugin.loadConfig();
				sender.sendMessage("All files reloaded");
			} else {
				sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "birthdays.reload.all");
			}
		} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("config")) {
				if (sender.hasPermission("birthdays.reload.config")) {
					plugin.loadConfig();
					sender.sendMessage("Config file reloaded");
				} else {
					sender.sendMessage(ChatColor.RED+  "Missing required permission node: " + ChatColor.WHITE + "birthdays.reload.config");
				}
			} else {
				// TODO: add other config reloads here
				sender.sendMessage("Invalid sub-command.  Please see " + ChatColor.RED + "/birthdays help " + ChatColor.RESET +  "for complete list of commands");
			}
		} else {
			sender.sendMessage("Invalid command.  Please see " + ChatColor.RED + "/birthdays help " + ChatColor.RESET +  "for complete list of commands");
		}
	}

	public void setBirthday(CommandSender sender, String args[]) {
		// only available to players
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can set there birthday");
			return;
		}
		// see if the player has already set there birthday, don't want them to be able to redefine it
		if (BirthdayManager.getInstance().getConfig().contains(sender.getName() + ".birthday")) {
			sender.sendMessage(ChatColor.RED + "Birthday has already been set.  You must get an admin to change if its wrong");
			return;
		} else {
			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Must provide your birthday in the format of MM/DD and nothing more");
				return;
			} else {
				// parse out and verify the data
				if (args[1].contains("/")) {
					int month = Integer.parseInt(args[1].substring(0, args[1].indexOf("/")));
					int day = Integer.parseInt(args[1].substring(args[1].indexOf("/") + 1));

					if ((month < 1 || month > 12) || (day < 1 || day > 31)) {
						sender.sendMessage(ChatColor.RED + "The date you entered is invalid");
						return;
					} else {
						BirthdayManager.getInstance().getConfig().set(sender.getName() + ".birthday", args[1]);
						BirthdayManager.getInstance().saveConfig();
						sender.sendMessage(ChatColor.AQUA + "Birthday successfully set");
						return;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Birthday entered incorrectly.  Must be in format of MM/DD");
				}

			}
		}
	}
}