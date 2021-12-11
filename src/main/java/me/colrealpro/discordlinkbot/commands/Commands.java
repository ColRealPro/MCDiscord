package me.colrealpro.discordlinkbot.commands;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.colrealpro.discordlinkbot.Main;
import me.colrealpro.discordlinkbot.Messages;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Commands implements CommandExecutor {

    private String getDurationString(int totalSecs) {

        long hours = totalSecs / 3600;
        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;

        String timeString = String.format("%02d minutes, %02d seconds", minutes, seconds);
        return timeString;
    }

    private Plugin plugin = Main.getPlugin(Main.class);
    private Guild guild = Main.guild;

    public static List<String> toggledMessages = new ArrayList<>();
    public static List<String> toggledVisible = new ArrayList<>();

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

    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { return true; }
        Player player = (Player) sender;

        // /show (Channel) (true/false)
        if (command.getName().equalsIgnoreCase("show")) {

            if (!(args.length >= 2)) {
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " Syntax: (Channel) (True/False)");
                return true;
            }

            String TrueFalse = args[1].toLowerCase().replaceAll(" ", "");

            if (!(TrueFalse.equals("true") || TrueFalse.equals("false"))) {
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " Syntax: (Channel) (True/False)");
                return true;
            }

            List<TextChannel> textChannelsByName = guild.getTextChannelsByName(args[0], true);

            if (textChannelsByName.isEmpty()) {
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " This channel doesn't exist!");
                return true;
            }

            if (textChannelsByName.size() > 1) {
                BaseComponent errorMessage = new TextComponent("");
                TextComponent Message = new TextComponent(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " Unable to show channel");
                TextComponent Info = new TextComponent(" [Show Info]");
                Info.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                Info.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click To Show Info").create()));
                Info.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/discordtomclink:errorinfo duplicate"));
                errorMessage.addExtra(Message);
                errorMessage.addExtra(Info);
                player.spigot().sendMessage(errorMessage);
                return true;
            }

            HashMap<Long, Boolean> data = new HashMap<>();

            if (Messages.toggledChannels.get(player.getUniqueId()) != null) {
                data = Messages.toggledChannels.get(player.getUniqueId());
            }

            data.put(textChannelsByName.get(0).getIdLong(), Boolean.parseBoolean(TrueFalse));

            Messages.toggledChannels.put(player.getUniqueId(), data);

            if (Boolean.parseBoolean(TrueFalse)) {
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "(!) " + ChatColor.RESET + ChatColor.GOLD + Capital(textChannelsByName.get(0).getName().replaceAll("-", " ")) + " will now be shown in chat");
            } else {
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "(!) " + ChatColor.RESET + ChatColor.GOLD + Capital(textChannelsByName.get(0).getName().replaceAll("-", " ")) + " will no longer be shown in chat");
            }

            return true;
        }

        // /chatlink (Setting) (True/False)
        if (command.getName().equalsIgnoreCase("chatlink")) {
            if (!(args.length >= 2)) {
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " Syntax: (Setting) (True/False)");
                return true;
            }

            String TrueFalse = args[1].toLowerCase().replaceAll(" ","");
            String Setting = args[0].toLowerCase().replaceAll(" ","");

            if (Setting.equals("sendmessages") || Setting.equals("visible")) {
                if (TrueFalse.equals("true") || TrueFalse.equals("false")) {
                    if (TrueFalse.equals("true")) {
                        if (Setting.equals("sendmessages")) {
                            //player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " This setting is disabled!");
                            if (!toggledMessages.contains(player.getName())) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " Your messages are already being sent to discord");
                                return true;
                            } else {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " Your messages are now being sent to discord");
                                toggledMessages.remove(player.getName());
                                Bukkit.dispatchCommand(console, "sendmessages " + player.getName() + " true");
                            }
                        }
                        if (Setting.equals("visible")) {
                            if (!toggledVisible.contains(player.getName())) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " You are already seeing messages sent inside of discord");
                                return true;
                            } else {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "(!)" + ChatColor.RESET + ChatColor.GOLD + " You can now see messages sent inside of discord");
                                toggledVisible.remove(player.getName());
                                return true;
                            }
                        }
                    } else {
                        if (Setting.equals("sendmessages")) {
                            if (!toggledMessages.contains(player.getName())) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " Your messages are no longer being sent to discord");
                                toggledMessages.add(player.getName());
                                Bukkit.dispatchCommand(console, "sendmessages " + player.getName() + " false");
                                return true;
                            } else {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " Your messages are already not being sent to discord");
                            }
                        }
                        if (Setting.equals("visible")) {
                            if (!toggledVisible.contains(player.getName())) {
                                //player.sendMessage("Debug:" + toggledGeneral.toString());
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " You will no longer see messages sent in discord");
                                toggledVisible.add(player.getName());
                                return true;
                            } else {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " You already have visibilty of messages sent in discord disabled!");
                            }
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " Please enter a true or false value!");
                }
            } else {
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "(!)" + ChatColor.RESET + ChatColor.GOLD + " Supported Settings: SendMessages, Visible");
            }
        }

        if (command.getName().equalsIgnoreCase("unverify")) {
            if(player.hasPermission("chatlink.unverify")) {
                if (args.length >= 1) {
                    Player target = Bukkit.getPlayer(args[0]);
                    StringBuilder reason;
                    if(target != null) {
                        if (!(args.length == 1)) {
                            reason = new StringBuilder();
                            for (int i = 1; i < args.length; i++) {
                                if (i == 1) {
                                    reason.append(args[i]);
                                } else {
                                    reason.append(" " + args[i]);
                                }
                            }
                        } else {
                            reason = new StringBuilder("Unspecified Reason");
                        }
                        Main.data.getConfig().set("Users." + target.getUniqueId() + ".unallowed", true);
                        Main.data.getConfig().set("Users." + target.getUniqueId() + ".Discord", null);
                        Main.data.getConfig().set("Users." + target.getUniqueId() + ".timeUnallowed", Instant.now().getEpochSecond());
                        Main.data.getConfig().set("Users." + target.getUniqueId() + ".reason", reason.toString());
                        int timeNumber = 3*60;
                        Main.data.getConfig().set("Users." + target.getUniqueId() + ".time", timeNumber);
                        String timeString = getDurationString(timeNumber);
                        Main.data.saveConfig();
                        target.kickPlayer(ChatColor.BLUE + "You have been unverified!\n" + ChatColor.WHITE + "Reason: " + ChatColor.YELLOW + reason.toString() + ChatColor.WHITE + "\nYou are not allowed to re verify for " + ChatColor.YELLOW + timeString + ChatColor.WHITE + "\nUse this time to read the rules again!");
                    }
                } else {
                    player.sendMessage("debug: no args");
                }
            }
        }

        return true;
    }
}
