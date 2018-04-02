package com.shadebyte.cosmicvault;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.shadebyte.cosmicvault.utils.VaultUtils;

public class PvCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOnly players may use this command!"));
			return true;
		}

		Player p = (Player) sender;

		if (!p.hasPermission("cosmicvaults.cmd")) {
			for (String all : Core.getInstance().getConfig().getStringList("messages.no-permission")) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', all));
			}
			return true;
		}

		if (args.length == 0) {
			p.openInventory(GUIS.instance.vaultSelection(p));
			return true;
		}

		if (args.length == 1) {

			if (args[0].equalsIgnoreCase("reload") && p.hasPermission("cosmicvaults.cmd.reload")) {
				Core.getDataConfig().reloadConfig();
				Core.getInstance().reloadConfig();
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dConfiguration files have been reloaded!"));
				return true;
			}

			if (!VaultUtils.isInt(args[0])) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a valid number!"));
				return true;
			}

			if (VaultUtils.canUseVault(p, Integer.parseInt(args[0]))) {
				if (Integer.parseInt(args[0]) <= 0) {
					for (String all : Core.getInstance().getConfig().getStringList("messages.vault-zero")) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', all));
					}
					return true;
				}
				p.openInventory(GUIS.instance.vaultPage(p, Integer.parseInt(args[0])));
				Core.getInstance().openedVault.put(p.getUniqueId(), Integer.parseInt(args[0]));
			} else {
				for (String all : Core.getInstance().getConfig().getStringList("messages.no-permission")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', all));
				}
			}
			return true;
		}

		return true;
	}
}
