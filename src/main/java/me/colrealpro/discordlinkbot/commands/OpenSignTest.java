package me.colrealpro.discordlinkbot.commands;

import me.colrealpro.discordlinkbot.Inventories.UnverifyInventories;
import me.colrealpro.discordlinkbot.Main;
import me.colrealpro.discordlinkbot.SignMenuFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class OpenSignTest extends UnverifyInventories implements CommandExecutor, Listener {

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("OpenSignTest")) {
            Inventory inventory = createReasonListInventory();
            UnverifyInventories.ActiveInventories.put(player.getUniqueId(), inventory);
            UnverifyInventories.ActiveInventoryTypes.put(player.getUniqueId(), "List");
            player.openInventory(inventory);
            return true;
        }

        return true;
    }
}
