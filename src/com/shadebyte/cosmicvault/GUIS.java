package com.shadebyte.cosmicvault;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.shadebyte.cosmicvault.utils.VaultUtils;

public class GUIS {

    public static GUIS instance = new GUIS();

    public Inventory iconSelectionGUI() {
        // Create the basic inventory variable thing with configurable size and
        // title.
        Inventory inv = Bukkit.createInventory(null, Core.getInstance().getConfig().getInt("icon-selection.size"),
                ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("icon-selection.title")));
        for (String keys : Core.getInstance().getConfig().getConfigurationSection("icon-selection.data").getKeys(false)) {
            int slot = Integer.parseInt(keys);
            String rawItem = Core.getInstance().getConfig().getString("icon-selection.data." + keys + ".item");
            String item[] = rawItem.split(":");
            ItemStack is = new ItemStack(Material.valueOf(item[0]), 1, Short.parseShort(item[1]));
            ItemMeta meta = is.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            for (String all : Core.getInstance().getConfig().getStringList("icon-selection.data." + keys + ".lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', all));
            }
            meta.setLore(lore);
            is.setItemMeta(meta);
            inv.setItem(slot - 1, is);
        }
        return inv;
    }

    public Inventory vaultPage(Player p, int vault) {
        Inventory inv = Bukkit.createInventory(null, VaultUtils.getMaxSize(p), ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("vault-title") + " #" + vault));
        if (Core.getDataConfig().getConfig().contains("players." + p.getUniqueId().toString() + "." + vault)) {
            if (!Core.getDataConfig().getConfig().contains("players." + p.getUniqueId().toString() + "." + vault + ".contents")) {
                return inv;
            }

            for (String keys : Core.getDataConfig().getConfig().getConfigurationSection("players." + p.getUniqueId().toString() + "." + vault + ".contents").getKeys(false)) {
                int slot = Integer.parseInt(keys);
                inv.setItem(slot, Core.getDataConfig().getConfig().getItemStack("players." + p.getUniqueId().toString() + "." + vault + ".contents." + keys));
            }
        } else {
            return inv;
        }
        return inv;
    }

    public Inventory vaultSelection(Player p) {
        Inventory inv = Bukkit.createInventory(null, VaultUtils.getMaxSelectionMenu(p), ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("vault-selection.title")));
        int slot = 0;
        int vault = 1;
        while (slot < VaultUtils.getMaxSelectionMenu(p)) {
            if (VaultUtils.canUseVault(p, vault)) {
                inv.setItem(slot, vaultItem(p, vault));
            } else {
                String rawItem = Core.getInstance().getConfig().getString("locked-item.item");
                String[] item = rawItem.split(":");
                ItemStack stack = new ItemStack(Material.valueOf(item[0].toUpperCase()), 1, Short.parseShort(item[1]));
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("locked-item.name")));
                ArrayList<String> lore = new ArrayList<>();
                for (String all : Core.getInstance().getConfig().getStringList("locked-item.lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', all));
                }
                meta.setLore(lore);
                stack.setItemMeta(meta);
                inv.setItem(slot, stack);
            }
            slot++;
            vault++;
        }
        return inv;
    }

    private ItemStack vaultItem(Player p, int vault) {
        String itemRaw = null;
        if (Core.getDataConfig().getConfig().contains("players." + p.getUniqueId().toString() + "." + vault)) {
            itemRaw = Core.getDataConfig().getConfig().getString("players." + p.getUniqueId().toString() + "." + vault + ".icon");
        } else {
            itemRaw = Core.getInstance().getConfig().getString("vault-selection.default-item");
        }
        String[] item = itemRaw.split(":");
        ItemStack is = new ItemStack(Material.valueOf(item[0]), 1, Short.parseShort(item[1]));
        ItemMeta meta = is.getItemMeta();
        if (Core.getDataConfig().getConfig().contains("players." + p.getUniqueId().toString() + "." + vault)) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getDataConfig().getConfig().getString("players." + p.getUniqueId().toString() + "." + vault + ".name")));
        } else {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("vault-selection.default-item-name").replace("{vaultnumber}", String.valueOf(vault))));
        }
        ArrayList<String> lore = new ArrayList<>();
        for (String all : Core.getInstance().getConfig().getStringList("vault-selection.lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', all.replace("{vaultnumber}", String.valueOf(vault))));
        }
        meta.setLore(lore);
        is.setItemMeta(meta);
        return is;
    }
}
