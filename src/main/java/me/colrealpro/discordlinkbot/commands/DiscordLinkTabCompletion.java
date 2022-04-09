package me.colrealpro.discordlinkbot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DiscordLinkTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,  String alias, String[] args) {

        if (args.length == 1) {
            List<String> channels = new ArrayList<>();

            channels.add("SendMessages");
            channels.add("Visible");
            channels.add("ShowRoles");

            return channels;
        } else if (args.length == 2) {
            List<String> trueFalse = new ArrayList<>();
            trueFalse.add("Enabled");
            trueFalse.add("Disabled");
            return trueFalse;
        }

        return null;
    }
}
