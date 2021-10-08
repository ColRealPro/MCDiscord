package me.colrealpro.discordlinkbot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ShowTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,  String alias, String[] args) {

        if (args.length == 1) {
           List<String> channels = new ArrayList<>();

           channels.add("General");

           return channels;
        } else if (args.length == 2) {
            List<String> trueFalse = new ArrayList<>();
            trueFalse.add("true");
            trueFalse.add("false");
            return trueFalse;
        }

        return null;
    }
}
