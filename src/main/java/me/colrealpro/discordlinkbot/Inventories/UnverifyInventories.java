package me.colrealpro.discordlinkbot.Inventories;

import me.colrealpro.discordlinkbot.Main;
import me.colrealpro.discordlinkbot.SignMenuFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class UnverifyInventories extends Functions implements Listener {

    private static class OpenInventoryRunnable implements Runnable {
        private final Player player;

        private OpenInventoryRunnable(Player player) {
            this.player = player;
        }
        @Override
        public void run() {
            player.openInventory(ActiveInventories.get(player.getUniqueId()));
        }
    }

    private Plugin plugin = Main.getPlugin(Main.class);

    public static HashMap<UUID, Inventory> ActiveInventories = new HashMap<>();
    public static HashMap<UUID, String> ActiveInventoryTypes = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) {
            if (plugin.getConfig().getBoolean("ShowDebug") == true) {
                player.sendMessage("Debug: null");
            }
            return;
        }
        if (!event.getClickedInventory().equals(ActiveInventories.get(player.getUniqueId()))) {
            if (plugin.getConfig().getBoolean("ShowDebug") == true) {
                player.sendMessage("Debug: ClickedInv not Active Inventory");
                player.sendMessage("Inventory Type: " + ActiveInventoryTypes.get(player.getUniqueId()));
            }
            return;
        }
        if (ActiveInventoryTypes.get(player.getUniqueId()).equals("Custom")) {

            event.setCancelled(true);

            switch (event.getSlot()) {
                case 10:
                    // Reason
                    player.sendMessage("Under Development");
                    player.closeInventory();
                    break;
                case 13:
                    // Name
                    player.sendMessage("Under Development!");
                    player.closeInventory();
                    break;
                case 16:
                    // Time
                    player.closeInventory();
                    SignMenuFactory.Menu menu = Main.signMenuFactory.newMenu(Arrays.asList("", "^^^^^^^^^^^^^^^", "Unverification", "Time (In Minutes)")).reopenIfFail(false)
                            .response((target, strings) -> {
                                if (!isInteger(strings[0])) {
                                    target.sendMessage("Not a valid integer, defaulting to 3");
                                    Inventory inventory = ActiveInventories.get(target.getUniqueId());
                                    ItemStack Item = inventory.getItem(16);
                                    ItemMeta Meta = Item.getItemMeta();
                                    List<String> lore = new ArrayList<String>(Meta.getLore());
                                    lore.set(lore.size()-1, ChatColor.GRAY + "Current: 3");
                                    Meta.setLore(lore);
                                    Item.setItemMeta(Meta);
                                    inventory.setItem(16, Item);
                                    ActiveInventories.put(target.getUniqueId(), inventory);
                                    OpenInventoryRunnable r = new OpenInventoryRunnable(target);

                                    getServer().getScheduler().scheduleSyncDelayedTask(plugin, r);

                                    return true;
                                }

                                Inventory inventory = ActiveInventories.get(target.getUniqueId());
                                ItemStack Item = inventory.getItem(16);
                                ItemMeta Meta = Item.getItemMeta();
                                List<String> lore = new ArrayList<String>(Meta.getLore());
                                lore.set(lore.size()-1, ChatColor.GRAY + "Current: " + strings[0]);
                                Meta.setLore(lore);
                                Item.setItemMeta(Meta);
                                inventory.setItem(16, Item);
                                ActiveInventories.put(target.getUniqueId(), inventory);
                                OpenInventoryRunnable r = new OpenInventoryRunnable(target);

                                getServer().getScheduler().scheduleSyncDelayedTask(plugin, r);

                                return true;
                            });

                    menu.open(player);
                    break;
            }

            return;
        } else if (ActiveInventoryTypes.get(player.getUniqueId()).equals("List")) {
            event.setCancelled(true);
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Create New")) {
                Inventory inventory = createCustomReasonInventory();
                ActiveInventories.put(player.getUniqueId(), inventory);
                ActiveInventoryTypes.put(player.getUniqueId(), "Custom");
                player.openInventory(inventory);
            }
        }
    }

    public HashMap loadReasons() {

        HashMap<String, String> ReasonInfo = new HashMap<String,String>();
        HashMap<String, HashMap<String,String>> Reasons = new HashMap<String, HashMap<String,String>>();

        ConfigurationSection sec = Main.data.getConfig().getConfigurationSection("formats");
        if (sec == null) return Reasons;

        Main.data.getConfig().getConfigurationSection("Reasons").getKeys(false).stream().anyMatch(key -> {

            // Get Data Of Reason
            ReasonInfo.put("Reason", Main.data.getConfig().getString("Reasons." + key + ".Reason"));
            ReasonInfo.put("Time", Main.data.getConfig().getString("Reasons." + key + ".Time"));
            // Store Data in hashmap
            Reasons.put(key, ReasonInfo);
            // Clear ReasonInfo
            ReasonInfo.clear();

            return false;
        });

        return Reasons;
    }

    public Inventory createReasonListInventory() {

        Inventory inventory;

        inventory = Bukkit.createInventory(null, 54, ChatColor.GRAY + "" + ChatColor.GOLD +  "Select" + ChatColor.GRAY +  " an unverifiction reason");

        HashMap<String, HashMap<String,String>> Reasons = loadReasons();

        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Create Custom
        item.setType(Material.OAK_SIGN);
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Create a custom reason");
        meta.setDisplayName(ChatColor.GOLD + "Create New");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(Reasons.size(), item);

        // Empty Slots
        item.setType(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        meta.setDisplayName(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);

        fillEmptySlots(inventory, item);

        return inventory;
    }

    public Inventory createCustomReasonInventory() {

        Inventory inventory;

        inventory = Bukkit.createInventory(null, 27, ChatColor.GRAY + "" + ChatColor.GOLD +  "Custom" + ChatColor.GRAY +  " Unverification Reason");

        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Reason
        item.setType(Material.BLUE_STAINED_GLASS_PANE);
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "The reason shown after a player is unverified");
        lore.add(ChatColor.RED + "No reason set!");
        meta.setDisplayName(ChatColor.BLUE + "Set Reason");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(10, item);

        item.setType(Material.ORANGE_STAINED_GLASS_PANE);
        lore.clear();
        lore.add(ChatColor.GRAY + "Name of the reason shown in the list menu");
        meta.setDisplayName(ChatColor.GOLD + "Set Name");
        lore.add(ChatColor.RED + "No name set!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(13, item);

        // Time
        item.setType(Material.RED_STAINED_GLASS_PANE);
        meta.setDisplayName(ChatColor.RED + "Set Time");
        lore.clear();
        lore.add(ChatColor.GRAY + "The amount of time before a player");
        lore.add(ChatColor.GRAY + "can re-verify");
        lore.add(ChatColor.GRAY + "Current: 3");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(16, item);

        // Close Barrier
        item.setType(Material.BARRIER);
        meta.setDisplayName(ChatColor.RED + "Close");
        lore.clear();
        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(22, item);

        // Empty Slots
        item.setType(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        meta.setDisplayName(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);

        fillEmptySlots(inventory, item);

        return inventory;
    }

}
