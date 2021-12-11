package me.colrealpro.discordlinkbot.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class errorinfo implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) { return true; }
        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("errorinfo")) {

            if (args.length != 1)
                return true;

            if (args[0].equalsIgnoreCase("duplicate")) {
                player.sendMessage(ChatColor.RED + "Error: More than one channels exist with this name!");
                return true;
            }
            return true;
        }

        return false;
    }
}
