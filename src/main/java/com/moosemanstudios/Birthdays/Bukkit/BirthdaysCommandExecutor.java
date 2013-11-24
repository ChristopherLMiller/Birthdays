package com.moosemanstudios.Birthdays.Bukkit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		String[] split = args;
		String commandName = cmd.getName().toLowerCase();
		this.sender = sender;
		
		if (commandName.equalsIgnoreCase("birthday")) {
			if (split.length == 0) {
				showHelp();
			} else {
				if (split[0].equalsIgnoreCase("help")) {
					showHelp();
				} else if (split[0].equalsIgnoreCase("version")) {
					showVersion();
				} else if (split[0].equalsIgnoreCase("update")) {
					update();
				} else if (split[0].equalsIgnoreCase("change")) {
					changeBirthday(split);
				} else if (split[0].equalsIgnoreCase("set")) {
					setBirthday(split);
				} else if (split[0].equalsIgnoreCase("view")) {
					viewBirthday(split);
				} else if (split[0].equalsIgnoreCase("claim")) {
					claimPrize();
				} else {
					sender.sendMessage("Uknown command");
				}
			}
			return true;
		}
		return false;
	}
	
	public void claimPrize() {
		if (sender instanceof Player) {
			if (plugin.getConfig().contains("players." + sender.getName())) {
				String dob = plugin.getConfig().getString("players." + sender.getName() + ".birthdate");
				
				if (dob.equalsIgnoreCase(getCurrentDate())) {
					plugin.giveGifts(sender.getName());
				} else {
					sender.sendMessage("It's not your birthday, nice try!");
				}
			} else {
				sender.sendMessage("You haven't set your birthday yet!");
			}			
		} else {
			sender.sendMessage(ChatColor.RED + "Must be a player to issue this command");
		}
	}
	
	public String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public void viewBirthday(String[] split) {
		if (sender.hasPermission("birthday.admin")) {
			if (split.length == 2) {
				String player = split[1];
				if (plugin.getConfig().contains("players." + player)) {
					sender.sendMessage("Player: " + player + " Birthdate: " + plugin.getConfig().getString("players." + player + ".birthdate"));
				} else {
					sender.sendMessage("Player entry not found");
				}
			} else {
				sender.sendMessage("Must specify player to lookup");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "birthday.admin");
		}
	}
	
	public void setBirthday(String[] split) {
		// make sure its a player first of all, can't handle consoles!
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			// see if there is a config option on that player already
			if (!plugin.getConfig().contains("players." + player.getName())) {
				
				// make sure they added a birthday
				if (split.length == 2) {
					String dob = split[1];

					if (verifyDOB(dob)) {
						// log this to the config file now
						plugin.getConfig().set("players." + player.getName() + ".birthdate", dob);
						plugin.saveConfig();
						sender.sendMessage("Birthday set!");
					} else {
						sender.sendMessage("Something isn't correct about the birthdate entered");
					}

				} else {
					sender.sendMessage("Must provide a birthday in format MM/DD/YYYY");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Already specified birthday, you must get an admin to change now");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Must be a player to use this command");
		}
	}
	
	public Boolean verifyDOB(String dob) {
		String[] parts = dob.split("/");
		
		if (parts.length == 3) {
			int month = Integer.parseInt(parts[0]);
			int day = Integer.parseInt(parts[1]);
			int year = Integer.parseInt(parts[2]);
			
			if (month < 1 || month > 12) {
				return false;
			}
			if (day < 1 || day > 31) {
				return false;
			}
			if (year < 1900 || year > 2100) {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public void changeBirthday(String[] split) {
		if (sender.hasPermission("birthdays.admin")) {
			if (split.length == 3) {
				String player = split[1];
				String dob = split[2];
				
				// make sure birthday was set first
				if (!plugin.getConfig().contains("players." + player)) {
					// verify the input			
					if (verifyDOB(dob)) {
						plugin.getConfig().set("players." + player + ".birthdate", dob);
						plugin.saveConfig();
						sender.sendMessage("Birthday changed for : " + player + "!");
					}					
				} else {
					sender.sendMessage("Player hasn't set a birthday yet");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Must provide both user and birthday in format MM/DD/YYYY");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "birthdays.admin");
		}
	}
	
	public void showHelp() {
		sender.sendMessage("/birthday help" + ChatColor.RED + ": Display this help screen");
		sender.sendMessage("/birthday version" + ChatColor.RED + ": Show plugin version");
		sender.sendMessage("/birthday set  MM/DD/YYYY" + ChatColor.RED + ": Set player birthday");
		
		if (sender.hasPermission("birthdays.admin")) {
			sender.sendMessage("/birthday view <player>" + ChatColor.RED + ": View specified players birthday");
			sender.sendMessage("/birthday change <player> <MM/DD/YYYY>" + ChatColor.RED + ": Update Player birthday");
			sender.sendMessage("/birthday update" + ChatColor.RED + ": Update plugin");
		}
	}
	
	public void showVersion() {
		sender.sendMessage(ChatColor.GOLD + "Birthdays Version: " + ChatColor.WHITE + plugin.getDescription().getVersion() + ChatColor.GOLD + " - Author: moose517");
	}
	
	public void update() {
		if (sender.hasPermission("birthdays.admin")) {
            if (plugin.updaterEnabled) { 
                    Updater updater = new Updater(plugin, plugin.curseID, plugin.getFileFolder(), Updater.UpdateType.NO_DOWNLOAD, false);
                    if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
                            sender.sendMessage(ChatColor.AQUA + "Update found, starting download: " + updater.getLatestName());
                            updater = new Updater(plugin, 35179, plugin.getFileFolder(), Updater.UpdateType.DEFAULT, true);
                            
                            switch (updater.getResult()) {
                            case FAIL_BADID:
                                    sender.sendMessage(ChatColor.AQUA + "ID was bad, report this to moose517 on dev.bukkit.org");
                                    break;
                            case FAIL_DBO:
                                    sender.sendMessage(ChatColor.AQUA + "Dev.bukkit.org couldn't be contacted, try again later");
                                    break;
                            case FAIL_DOWNLOAD:
                                    sender.sendMessage(ChatColor.AQUA + "File download failed");
                                    break;
                            case FAIL_NOVERSION:
                                    sender.sendMessage(ChatColor.AQUA + "Unable to check version on dev.bukkit.org, notify moose517");
                                    break;
                            case NO_UPDATE:
                                    break;
                            case SUCCESS:
                                    sender.sendMessage(ChatColor.AQUA + "Update downloaded successfully, restart server to apply update");
                                    break;
                            case UPDATE_AVAILABLE:
                                    sender.sendMessage(ChatColor.AQUA + "Update found but not downloaded");
                                    break;
                            default:
                                    sender.sendMessage(ChatColor.RED + "Shoudn't have had this happen, contact moose517");
                                    break;
                            }
                    } else {
                            sender.sendMessage(ChatColor.AQUA + "No updates found");
                    }
            } else {
                    sender.sendMessage(ChatColor.AQUA + "Updater not enabled.  Enabled in config");
            }
    } else {
            sender.sendMessage(ChatColor.RED + "Missing required permission: " + ChatColor.WHITE + "birthdays.admin");
    }
	}
}
