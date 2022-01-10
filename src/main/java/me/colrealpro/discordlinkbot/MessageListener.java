package me.colrealpro.discordlinkbot;

import me.colrealpro.discordlinkbot.commands.ConsoleCommands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MessageListener implements Listener {

    private final Plugin plugin = Main.getPlugin(Main.class);

    private final Guild guild = Main.guild;
    private final TextChannel messageChannel = Main.channel;

    private final HashMap<UUID, String> lastChnanels = new HashMap<>();

    private final Map<String, String> keyToDisplay = new HashMap<>();

    ConfigurationSection advancementMap = Main.messagesData.getConfig().getConfigurationSection("AdvancementKeys");

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


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        String Message = event.getMessage();
        if (event.getMessage().startsWith("#")) {
            // Player is trying to send a message to a channel
            Message = Message.substring(1);
            String[] args = Message.split(" ");

            if (args[0].equalsIgnoreCase("")) {
                // send to previous channel
                List<TextChannel> channelsCheck = guild.getTextChannelsByName(lastChnanels.get(player.getUniqueId()), true);
                if (channelsCheck.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "Unable to find previous channel");
                    return;
                }
                TextChannel channel = guild.getTextChannelsByName(lastChnanels.get(player.getUniqueId()), true).get(0);
                if (lastChnanels.containsKey(player.getUniqueId())) {
                    lastChnanels.remove(player.getUniqueId());
                }
                lastChnanels.put(player.getUniqueId(), channel.getName());
                MessageBuilder splitMessage = new MessageBuilder();
                String arr[] = Message.split(" ", 2);
                splitMessage.append("**<" + player.getName() + ">** " + arr[1]);
                splitMessage.stripMentions(guild.getJDA());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        channel.sendMessage(splitMessage.build()).queue();
                    }
                }.runTaskLater(this.plugin, 1);

                String channelName = Capital(channel.getName().replaceAll("-", " "));

                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "To " +  channelName + ChatColor.WHITE + "] " + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + arr[1]);
                event.setCancelled(true);
                return;
            }

            List<TextChannel> textChannelsByName = guild.getTextChannelsByName(args[0], true);

            if (textChannelsByName.isEmpty()) {
                if (args[0].equalsIgnoreCase("keep") || args[0].equalsIgnoreCase("") || args[0].equalsIgnoreCase("k")) {
                    // send to previous channel
                    List<TextChannel> channelsCheck = guild.getTextChannelsByName(lastChnanels.get(player.getUniqueId()), true);
                    if (channelsCheck.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "Unable to find previous channel");
                        return;
                    }
                    TextChannel channel = guild.getTextChannelsByName(lastChnanels.get(player.getUniqueId()), true).get(0);
                    if (lastChnanels.containsKey(player.getUniqueId())) {
                        lastChnanels.remove(player.getUniqueId());
                    }
                    lastChnanels.put(player.getUniqueId(), channel.getName());
                    MessageBuilder splitMessage = new MessageBuilder();
                    String arr[] = Message.split(" ", 2);
                    splitMessage.append("**<" + player.getName() + ">** " + arr[1]);
                    splitMessage.stripMentions(guild.getJDA());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            channel.sendMessage(splitMessage.build()).queue();
                        }
                    }.runTaskLater(this.plugin, 1);
                    String firstLetStr = channel.getName().substring(0, 1);
                    // Get remaining letter using substring
                    String remLetStr = channel.getName().substring(1);

                    // convert the first letter of String to uppercase
                    firstLetStr = firstLetStr.toUpperCase();

                    // concatenate the first letter and remaining string
                    String channelName = firstLetStr + remLetStr;
                    Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "To " +  channelName + ChatColor.WHITE + "] " + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + arr[1]);
                    event.setCancelled(true);
                } else {
                    player.sendMessage("Debug: unable to find channel!");
                }
                return;
            }
            TextChannel channel = guild.getTextChannelsByName(args[0], true).get(0);
            if (lastChnanels.containsKey(player.getUniqueId())) {
                lastChnanels.remove(player.getUniqueId());
            }
            lastChnanels.put(player.getUniqueId(), channel.getName());
            MessageBuilder splitMessage = new MessageBuilder();
            String arr[] = Message.split(" ", 2);
            splitMessage.append("**<" + player.getName() + ">** " + arr[1]);
            splitMessage.stripMentions(guild.getJDA());

            new BukkitRunnable() {
                @Override
                public void run() {
                    channel.sendMessage(splitMessage.build()).queue();
                }
            }.runTaskLater(this.plugin, 1);
            String firstLetStr = channel.getName().substring(0, 1);
            // Get remaining letter using substring
            String remLetStr = channel.getName().substring(1);

            // convert the first letter of String to uppercase
            firstLetStr = firstLetStr.toUpperCase();

            // concatenate the first letter and remaining string
            String channelName = firstLetStr + remLetStr;
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "To " +  channelName + ChatColor.WHITE + "] " + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + arr[1]);
            event.setCancelled(true);
            return;
        }

        List<String> toggledMessages = ConsoleCommands.toggledMessages;
        if (event.isCancelled()) return;
        if (!toggledMessages.contains(player.getName())) {
            MessageBuilder message = new MessageBuilder();
            message.append("**<" + player.getName() + ">** " + Message);
            message.stripMentions(guild.getJDA());
            new BukkitRunnable() {
                @Override
                public void run() {
                    messageChannel.sendMessage(message.build()).queue();
                }
            }.runTaskLater(this.plugin, 1);
        }
    }
    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        List<String> toggledMessages = ConsoleCommands.toggledMessages;
        event.getAdvancement();
        if (event.getAdvancement().getKey().getKey().contains("recipe/") || event.getPlayer() == null || Main.messagesData.getConfig().getString("AdvancementKeys." + event.getAdvancement().getKey().getKey()) == null) return;
        if (!toggledMessages.contains(player.getName())) {

            EmbedBuilder advanceEmbed = new EmbedBuilder();

            advanceEmbed
                    .setDescription("**" + player.getName() + "** has completed the advancement " + Main.messagesData.getConfig().getString("AdvancementKeys." + event.getAdvancement().getKey().getKey()))
                    .setAuthor("Advancement complete!", "", "")
                    .setColor(new Color(154, 246, 78));
            messageChannel.sendMessage(advanceEmbed.build()).complete();

        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!plugin.getConfig().getBoolean("showjoinmessages")) return;
        Player player = event.getPlayer();
        int playerCount = Bukkit.getOnlinePlayers().size();

        EmbedBuilder JoinEmbed = new EmbedBuilder();

        JoinEmbed
                .setDescription("**" + player.getName() + "** has joined the server! (" + playerCount + " players online)")
                .setAuthor("Player Joined!")
                .setColor(new Color(99, 214, 49));
        messageChannel.sendMessage(JoinEmbed.build()).complete();

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!plugin.getConfig().getBoolean("showjoinmessages")) return;
        Player player = event.getPlayer();
        int playerCount = Bukkit.getOnlinePlayers().size() - 1;
        EmbedBuilder leaveEmbed = new EmbedBuilder();

        leaveEmbed
                .setDescription("**" + player.getName() + "** has left the server! (" + playerCount + " players online)")
                .setAuthor("Player Left!")
                .setColor(new Color(255, 77, 77));
        messageChannel.sendMessage(leaveEmbed.build()).complete();
    }
}
