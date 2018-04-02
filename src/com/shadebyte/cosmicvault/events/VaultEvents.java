package com.shadebyte.cosmicvault.events;

import com.shadebyte.cosmicvault.Core;
import com.shadebyte.cosmicvault.GUIS;
import com.shadebyte.cosmicvault.utils.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class VaultEvents implements Listener {

    @EventHandler
    public void onVaultPageClose(InventoryCloseEvent e) {
        if (!Core.getInstance().openedVault.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }

        int vault = Core.getInstance().openedVault.get(e.getPlayer().getUniqueId());
        String name = Core.getInstance().getConfig().getString("vault-selection.default-item-name").replace("{vaultnumber}", String.valueOf(vault));
        String icon = Core.getInstance().getConfig().getString("vault-selection.default-item");


        if (!Core.getDataConfig().getConfig().contains("players." + e.getPlayer().getUniqueId().toString() + "." + vault)) {
            Core.getDataConfig().getConfig().set("players." + e.getPlayer().getUniqueId().toString() + "." + vault + ".icon", icon);
            Core.getDataConfig().getConfig().set("players." + e.getPlayer().getUniqueId().toString() + "." + vault + ".name", ChatColor.translateAlternateColorCodes('&', name));
        }

        for (int i = 0; i < e.getInventory().getSize(); i++) {
            Core.getDataConfig().getConfig().set("players." + e.getPlayer().getUniqueId().toString() + "." + vault + ".contents." + i, e.getInventory().getItem(i));
        }
        Core.getDataConfig().saveConfig();


        Core.getInstance().openedVault.remove(e.getPlayer().getUniqueId());
        if (!Core.getInstance().getConfig().getBoolean("disable-vault-selection-on-close")) {
            Bukkit.getServer().getScheduler().runTaskLater(Core.getInstance(), () -> {
                if (!Core.getInstance().vaultedit.containsKey(e.getPlayer().getUniqueId())) {
                    e.getPlayer().openInventory(GUIS.instance.vaultSelection((Player) e.getPlayer()));
                }
            }, 1);
        }
    }

    @EventHandler
    public void onVaultPageClick(InventoryClickEvent e) {
        try {
            Player p = (Player) e.getWhoClicked();
            if (!Core.getInstance().openedVault.containsKey(p.getUniqueId())) {
                return;
            }

            if (e.getRawSlot() < e.getView().getTopInventory().getSize()) {
                return;
            }

            for (String items : Core.getInstance().getConfig().getStringList("blocked-vault-items")) {
                String[] item = items.split(":");
                if (e.getCurrentItem().getType() == Material.valueOf(item[0]) && e.getCurrentItem().getDurability() == Short.parseShort(item[1])) {
                    e.setCancelled(true);
                }
            }
        } catch (Exception ex) {
        }
    }

    @EventHandler
    public void onIconSelectionClose(InventoryCloseEvent e) {
        if (!e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("icon-selection.title")))) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                e.getPlayer().openInventory(GUIS.instance.vaultSelection((Player) e.getPlayer()));
                Core.getInstance().openedVault.remove(e.getPlayer().getUniqueId());
                Core.getInstance().vaultedit.remove(e.getPlayer().getUniqueId());
            }
        }.runTaskLater(Core.getInstance(), 1);
    }

    @EventHandler
    public void onVaultSelectionClick(InventoryClickEvent e) {
        try {
            if (!e.getClickedInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("vault-selection.title")))) {
                return;
            }

            if (e.getCurrentItem() == null) {
                return;
            }

            if (e.getClickedInventory() == null) {
                return;
            }

            Player p = (Player) e.getWhoClicked();
            int slot = e.getSlot() + 1;

            e.setCancelled(true);

            if (!VaultUtils.canUseVault(p, slot)) {
                for (String all : Core.getInstance().getConfig().getStringList("messages.no-permission")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', all));
                }
                return;
            }

            if (e.getClick() == ClickType.LEFT) {
                p.openInventory(GUIS.instance.vaultPage(p, slot));
                Core.getInstance().openedVault.put(p.getUniqueId(), slot);
                return;
            } else if (e.getClick() == ClickType.RIGHT) {
                if (Core.getInstance().getConfig().getBoolean("disable-icon-selection")) {
                    return;
                }
                p.openInventory(GUIS.instance.vaultPage(p, slot));
                Core.getInstance().openedVault.put(p.getUniqueId(), slot);
                p.openInventory(GUIS.instance.iconSelectionGUI());
                Core.getInstance().vaultedit.put(p.getUniqueId(), slot);
            } else if (e.getClick() == ClickType.MIDDLE) {
                p.openInventory(GUIS.instance.vaultPage(p, slot));
                Core.getInstance().openedVault.put(p.getUniqueId(), slot);
                p.closeInventory();
                Core.getInstance().vaultedit.put(p.getUniqueId(), slot);
                for (String changingName : Core.getInstance().getConfig().getStringList("messages.changing-name")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', changingName));
                }
            }
        } catch (Exception ex) {

        }
    }

    @EventHandler
    public void onIconSelect(InventoryClickEvent e) {
        try {
            if (!e.getClickedInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("icon-selection.title")))) {
                return;
            }

            if (e.getCurrentItem() == null) {
                return;
            }

            if (e.getClickedInventory() == null) {
                return;
            }

            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();

            ItemStack is = e.getCurrentItem();
            int vault = Core.getInstance().vaultedit.get(p.getUniqueId());
            String item = String.valueOf(is.getType().name() + ":" + is.getDurability());
            String name = Core.getDataConfig().getConfig().getString("players." + p.getUniqueId().toString() + "." + vault + ".name");

            Core.getDataConfig().getConfig().set("players." + p.getUniqueId().toString() + "." + vault + ".icon", item);
            Core.getDataConfig().getConfig().set("players." + p.getUniqueId().toString() + "." + vault + ".name", name);
            Core.getDataConfig().saveConfig();
            Core.getInstance().vaultedit.remove(p.getUniqueId());
            p.openInventory(GUIS.instance.vaultSelection(p));
            for (String icon : Core.getInstance().getConfig().getStringList("messages.icon-change")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', icon.replace("{item}", item).replace("{vaultnumber}", String.valueOf(vault))));
            }
        } catch (Exception ex) {

        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!Core.getInstance().vaultedit.containsKey(p.getUniqueId())) {
            return;
        }

        e.setCancelled(true);

        if (e.getMessage().equalsIgnoreCase(Core.getInstance().getConfig().getString("cancel-word"))) {
            Core.getInstance().vaultedit.remove(p.getUniqueId());
            p.openInventory(GUIS.instance.vaultSelection(p));
            for (String cancel : Core.getInstance().getConfig().getStringList("messages.name-cancel")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', cancel));
            }
            return;
        }

        int vault = Core.getInstance().vaultedit.get(p.getUniqueId());
        String msg = e.getMessage();
        Core.getDataConfig().getConfig().set("players." + p.getUniqueId().toString() + "." + vault + ".name", msg);
        Core.getDataConfig().saveConfig();
        Core.getInstance().vaultedit.remove(p.getUniqueId());
        for (String name : Core.getInstance().getConfig().getStringList("messages.name-changed")) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', name.replace("{name}", e.getMessage()).replace("{vaultnumber}", String.valueOf(vault))));
        }
        p.openInventory(GUIS.instance.vaultSelection(p));

    }
}
