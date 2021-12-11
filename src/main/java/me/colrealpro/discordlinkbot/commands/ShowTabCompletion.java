package me.colrealpro.discordlinkbot.commands;

import me.colrealpro.discordlinkbot.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ShowTabCompletion implements TabCompleter {

    Guild guild = Main.guild;

    public String Capital(String message) {
        // stores each characters to a char array
        char[] charArray = message.toCharArray();
        boolean foundSpace = true;

        for(int i = 0; i < charArray.length; i++) {

            // if the array element is a letter
            if(Character.isLetter(charArray[i])) {

                // check space is present before the letter
                if(foundSpace) {

                    // change the letter into uppercase
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
            }

            else {
                // if the new character is not character
                foundSpace = true;
            }
        }

        // convert the char array to the string
        message = String.valueOf(charArray);
        return message;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,  String alias, String[] args) {

        if (args.length == 1) {
           List<String> channels = new ArrayList<>();

            List<TextChannel> guildChannels = guild.getTextChannels();

            for (TextChannel channel : guildChannels) {
                channels.add(Capital(channel.getName()));
            }


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
