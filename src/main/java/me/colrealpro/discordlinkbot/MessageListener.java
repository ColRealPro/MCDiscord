package me.colrealpro.discordlinkbot;

import me.colrealpro.discordlinkbot.commands.ConsoleCommands;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static me.colrealpro.discordlinkbot.StartBot.jda;

public class MessageListener implements Listener {
    String webhookUrl = "https://discord.com/api/webhooks/885681755489726484/-MMbXCtRjEpdMxhojUS8yXtgK9Cr5mk018PnN180voN9VXrm3ZPJMTAVPJ_uaHyP99R8";

    private Plugin plugin = Main.getPlugin(Main.class);
    private Logger logger = plugin.getLogger();

    private HashMap<UUID, String> lastChnanels = new HashMap<UUID, String>();

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        String Message = event.getMessage();
        if (event.getMessage().startsWith("#")) {
            // Player is trying to send a message to a channel
            Message = Message.substring(1);
            String[] args = Message.split(" ");
            Guild guild = jda.getGuildById("790779821348487238");

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
                Message = Message.replaceFirst(args[0] + " ", "");
                Message = Message.replaceAll("@everyone", "`@everyone`").replaceAll("@here", "`@here`").replaceAll("\"", "''");
                channel.sendMessage("**<" + player.getName() + ">** " + Message).complete();
                String firstLetStr = channel.getName().substring(0, 1);
                // Get remaining letter using substring
                String remLetStr = channel.getName().substring(1);

                // convert the first letter of String to uppercase
                firstLetStr = firstLetStr.toUpperCase();

                // concantenate the first letter and remaining string
                String channelName = firstLetStr + remLetStr;
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "To " +  channelName + ChatColor.WHITE + "] " + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + Message);
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
                    Message = Message.replaceFirst(args[0] + " ", "");
                    Message = Message.replaceAll("@everyone", "`@everyone`").replaceAll("@here", "`@here`").replaceAll("\"", "''");
                    channel.sendMessage("**<" + player.getName() + ">** " + Message).complete();
                    String firstLetStr = channel.getName().substring(0, 1);
                    // Get remaining letter using substring
                    String remLetStr = channel.getName().substring(1);

                    // convert the first letter of String to uppercase
                    firstLetStr = firstLetStr.toUpperCase();

                    // concantenate the first letter and remaining string
                    String channelName = firstLetStr + remLetStr;
                    Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "To " +  channelName + ChatColor.WHITE + "] " + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + Message);
                    event.setCancelled(true);
                    return;
                } else {
                    player.sendMessage("Debug: unable to find channel!");
                    return;
                }
            }
            TextChannel channel = guild.getTextChannelsByName(args[0], true).get(0);
            if (lastChnanels.containsKey(player.getUniqueId())) {
                lastChnanels.remove(player.getUniqueId());
            }
            lastChnanels.put(player.getUniqueId(), channel.getName());
            Message = Message.replaceFirst(args[0] + " ", "");
            Message = Message.replaceAll("@everyone", "`@everyone`").replaceAll("@here", "`@here`").replaceAll("\"", "''");
            channel.sendMessage("**<" + player.getName() + ">** " + Message).complete();
            String firstLetStr = channel.getName().substring(0, 1);
            // Get remaining letter using substring
            String remLetStr = channel.getName().substring(1);

            // convert the first letter of String to uppercase
            firstLetStr = firstLetStr.toUpperCase();

            // concantenate the first letter and remaining string
            String channelName = firstLetStr + remLetStr;
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "To " +  channelName + ChatColor.WHITE + "] " + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + Message);
            event.setCancelled(true);
            return;
        }

        List<String> toggledMessages = ConsoleCommands.toggledMessages;
        if (!toggledMessages.contains(player.getName())) {
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
            Message = Message.replaceAll("@everyone", "`@everyone`").replaceAll("@here", "`@here`").replaceAll("\"", "''");
            webhook.setContent("**<" + player.getName() + ">** " + Message);
            try {
                webhook.execute();
            } catch (java.io.IOException e) {
                logger.severe(e.getStackTrace().toString());
            }
        }
    }
    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        List<String> toggledMessages = ConsoleCommands.toggledMessages;
        if (!toggledMessages.contains(player.getName())) {
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle("THIS IS NOT AN ACTIVE FEATURE AND IS IN BETA")
                    .setDescription("**" + player.getName() + "** has completed the achievement " + event.getAdvancement().toString())
                    .setAuthor("Advancement complete!", "", "")
                    .setColor(new Color(154, 246, 78))
            );
            try {
                webhook.execute();
            } catch (java.io.IOException e) {
                logger.severe(e.getStackTrace().toString());
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int playerCount = Bukkit.getOnlinePlayers().size();
        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setDescription("**" + player.getName() + "** has joined the server! (" + playerCount + " players online)")
                .setAuthor("Player joined!", "", "")
                .setColor(new Color(99, 214, 49))
        );
        try {
            webhook.execute();
        }
        catch(java.io.IOException e) {
            logger.severe(e.getStackTrace().toString());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        int playerCount = Bukkit.getOnlinePlayers().size() - 1;
        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setDescription("**" + player.getName() + "** has left the server! (" + playerCount + " players online)")
                .setAuthor("Player left", "", "")
                .setColor(new Color(255, 77, 77))
        );
        try {
            webhook.execute();
        }
        catch(java.io.IOException e) {
            logger.severe(e.getStackTrace().toString());
        }
    }
}
