package com.moosemanstudios.Birthdays.Bukkit;

import com.moosemanstudios.Birthdays.Core.BirthdayManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

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
				else if (args[0].equalsIgnoreCase("reload"))
					reload(sender, args);
				else if (args[0].equalsIgnoreCase("set"))
					setBirthday(sender, args);
				else if (args[0].equalsIgnoreCase("change"))
					changeBirthday(sender, args);
				else if (args[0].equalsIgnoreCase("claim"))
					claimPresent(sender, args);
                else
                    sender.sendMessage(ChatColor.RED + "Invalid command.  Please see " + ChatColor.WHITE + "/birthdays help" + ChatColor.RED + " for all available commands");
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

		if (sender.hasPermission("birthdays.set")) {
			sender.sendMessage("/birthdays change <player> <MM/DD>" + ChatColor.RED + ": Set players birthday to that specified");
		}
		if (sender.hasPermission("birthdays.reload.all")) {
			sender.sendMessage("/birthdays reload" + ChatColor.RED + ": Reload all configuration files");
		}
		if (sender.hasPermission("birthdays.reload.config")) {
			sender.sendMessage("/birthdays reload config" + ChatColor.RED + ": Reload the config from disk");
		}
		if (sender.hasPermission("birthdays.reload.players")) {
			sender.sendMessage("/birthdays reload players" + ChatColor.RED + ": Reload the players file from disk");
		}
    }

    public void showVersion(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Birthdays Version:" + ChatColor.WHITE + plugin.pdfFile.getVersion() + ChatColor.GOLD + " Author: moose517");
    }

	public void claimPresent(CommandSender sender, String args[]) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			// make sure they aren't in creative first of all
			if (player.getGameMode() != GameMode.CREATIVE) {

				// verify its actually the persons birthday and they haven't claimed already
				String birthday = BirthdayManager.getInstance().getConfig().getString(sender.getName() + ".birthday");
				int claimedYear = BirthdayManager.getInstance().getConfig().getInt(sender.getName() + ".claimed");

				// get and format the current date to match what we store
				String date = new SimpleDateFormat("MM/dd").format(Calendar.getInstance().getTime());
				int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));

				if ((birthday.equals(date)) && (claimedYear < year)) {

					// give the players there items
					if (plugin.itemGiftEnabled) {
						ItemStack item = new ItemStack(plugin.itemGiftType, plugin.itemGiftAmount);
						Inventory inv = player.getInventory();

						HashMap<Integer, ItemStack> leftOver = inv.addItem(item);

						if (leftOver.isEmpty()) {
							player.sendMessage(ChatColor.AQUA + "Your gift has been given");
						} else {
							player.getWorld().dropItem(player.getLocation(), new ItemStack(plugin.itemGiftType, leftOver.get(0).getAmount()));
							player.sendMessage(ChatColor.AQUA + "Your inventory was too full for the gift, it has been dropped on the ground. Ooops.");
						}
					}

					if (plugin.currencyGiftEnabled) {
						EconomyResponse response = plugin.economy.depositPlayer(player.getName(), plugin.currencyGiftAmount);
						if (response.transactionSuccess()) {
							player.sendMessage(ChatColor.AQUA + "Money have been gifted to your account!");
						} else {
							player.sendMessage(ChatColor.AQUA + "Something has gone wrong tying to give you money for your birthday :(");
						}
					}

					// finally lets update the file so that they can't claim again
					BirthdayManager.getInstance().getConfig().set(player.getName() + ".claimed", year);
					BirthdayManager.getInstance().saveConfig();
				} else {
					player.sendMessage(ChatColor.RED + "You have already claimed your gifts for this year!");
				}
			} else {
				sender.sendMessage(ChatColor.AQUA + "Gifts won't be given in creative mode.  Just to ensure you receive them :)");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Command only available to players");
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
			} else if (args[1].equalsIgnoreCase("player")) {
				if (sender.hasPermission("birthdays.reload.players")) {
					BirthdayManager.getInstance().reloadConfig();
					sender.sendMessage("Players file reloaded");
				} else {
					sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "birthdays.reload.players");
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

	public void changeBirthday(CommandSender sender, String args[]) {
		if (sender.hasPermission("birthdays.set")) {
			if (args.length == 3) {
				String player = args[1];

				if (args[2].contains("/")) {
					int month = Integer.parseInt(args[2].substring(0, args[2].indexOf("/")));
					int day = Integer.parseInt(args[2].substring(args[2].indexOf("/") + 1));

					if ((month < 1 || month > 12) || (day < 1 || day > 31)) {
						sender.sendMessage(ChatColor.RED + "The date you entered is invalid");
						return;
					} else {
						BirthdayManager.getInstance().getConfig().set(player + ".birthday", args[2]);
						BirthdayManager.getInstance().saveConfig();
						sender.sendMessage(ChatColor.AQUA + "Birthday successfully set");
						return;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Birthday entered incorrectly.  Must be in format of MM/DD");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid syntax, see help for proper usage");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "birthdays.set");
		}
	}
}