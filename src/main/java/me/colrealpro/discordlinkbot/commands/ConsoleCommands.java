package me.colrealpro.discordlinkbot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ConsoleCommands implements CommandExecutor {

    public static List<String> toggledMessages = new ArrayList<String>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("sendmessages")) {
            if (!(args.length >= 2)) { return true; }
            String TrueFalse = args[1].toLowerCase().replaceAll(" ","");
            String Player = args[0].replaceAll(" ","");

            if (TrueFalse.equals("true")) {
                if (toggledMessages.contains(Player)) {
                    toggledMessages.remove(Player);
                }
            } else {
                if (!toggledMessages.contains(Player)) {
                    toggledMessages.add(Player);
                }
            }
        }
        return true;
    }
}
