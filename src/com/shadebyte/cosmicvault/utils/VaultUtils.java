package com.shadebyte.cosmicvault.utils;

import org.bukkit.entity.Player;

import com.shadebyte.cosmicvault.Core;

public class VaultUtils {

	/**
	 * Quick method to check if string is an integer.
	 */
	public static boolean isInt(String number) {
		try {
			Integer.parseInt(number);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Check if the player can use the vault.
	 */
	public static boolean canUseVault(Player p, int number) {
		if (p.hasPermission("cosmicvaults.amt." + String.valueOf(number))) {
			return true;
		}
		for (int x = number; x <= 99; x++) {
			if (p.hasPermission("cosmicvaults.amt." + String.valueOf(x))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check the maximum size vault a player can have.
	 */
	public static int getMaxSize(Player p) {
		int size = Core.getInstance().getConfig().getInt("default-vault-size");
		if (p.hasPermission("cosmicvaults.size.9")) {
			size = 9;
		}
		if (p.hasPermission("cosmicvaults.size.18")) {
			size = 18;
		}
		if (p.hasPermission("cosmicvaults.size.27")) {
			size = 27;
		}
		if (p.hasPermission("cosmicvaults.size.36")) {
			size = 36;
		}
		if (p.hasPermission("cosmicvaults.size.45")) {
			size = 45;
		}
		if (p.hasPermission("cosmicvaults.size.54")) {
			size = 54;
		}
		return size;
	}
	
	/**
	 * Check the maximum size vault a player can have.
	 */
	public static int getMaxSelectionMenu(Player p) {
		int size = Core.getInstance().getConfig().getInt("default-select-menu-size");
		if (p.hasPermission("cosmicvaults.selectionsize.9")) {
			size = 9;
		}
		if (p.hasPermission("cosmicvaults.selectionsize.18")) {
			size = 18;
		}
		if (p.hasPermission("cosmicvaults.selectionsize.27")) {
			size = 27;
		}
		if (p.hasPermission("cosmicvaults.selectionsize.36")) {
			size = 36;
		}
		if (p.hasPermission("cosmicvaults.selectionsize.45")) {
			size = 45;
		}
		if (p.hasPermission("cosmicvaults.selectionsize.54")) {
			size = 54;
		}
		return size;
	}
}
