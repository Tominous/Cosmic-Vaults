package com.shadebyte.cosmicvault;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.shadebyte.cosmicvault.events.VaultEvents;
import com.shadebyte.cosmicvault.utils.ConfigWrapper;

public class Core extends JavaPlugin {

	// Private non initialized instance of this class.
	private static Core instance;

	// Create private instance of ConfigWrapper to make data file.
	private static ConfigWrapper dataConfig;

	// Temporary storage for vault currently opened.
	public HashMap<UUID, Integer> openedVault = new HashMap<>();
	public HashMap<UUID, Integer> vaultedit = new HashMap<>();

	@Override
	public void onEnable() {
		instance = this;
		// Generate the configuration file, and save it's defaults.
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		
		// Initialize the dataConfig, save and reload it.
		dataConfig = new ConfigWrapper(this, "", "Data.yml");
		dataConfig.saveConfig();
		dataConfig.reloadConfig();

		// Register Events
		Bukkit.getPluginManager().registerEvents(new VaultEvents(), this);

		// Register the command
		getCommand("pv").setExecutor(new PvCMD());
	}
	
	@Override
	public void onDisable() {
		instance = null;
	}

	/**
	 * Get the instance of this class.
	 */
	public static Core getInstance() {
		return instance;
	}

	/**
	 * Get the instance of the Data Config (Config Wrapper)
	 */
	public static ConfigWrapper getDataConfig() {
		return dataConfig;
	}

	public static void main(String[] args) {
	}
}
