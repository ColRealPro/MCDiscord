package me.colrealpro.discordlinkbot;

import me.colrealpro.discordlinkbot.commands.Commands;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Messages extends ListenerAdapter implements Listener {
    List<String> toggledGeneral = Commands.toggledGeneral;
    //List<String> toggledMessages = Commands.toggledMessages;
    List<String> toggledVisible = Commands.toggledVisible;

    private Plugin plugin = Main.getPlugin(Main.class);

    protected String getSaltString(int max) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < max) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    private String getDurationString(int totalSecs) {

        long hours = totalSecs / 3600;
        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;

        String timeString = String.format("%02d minutes, %02d seconds", minutes, seconds);
        return timeString;
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        boolean Verified = Main.data.getConfig().isSet("Users." + player.getUniqueId() + ".Discord");
        boolean VerifiedCode = Main.data.getConfig().isSet("Users." + player.getUniqueId() + ".VerificationCode");
        boolean unallowed = Main.data.getConfig().isSet("Users." + player.getUniqueId() + ".unallowed");
        String verifyCode = getSaltString(plugin.getConfig().getInt("VerificationCodeLength"));

        if (unallowed == true) {
            long StartTime = Main.data.getConfig().getLong("Users." + player.getUniqueId() + ".timeUnallowed");
            String reason = Main.data.getConfig().getString("Users." + player.getUniqueId() + ".reason");
            if (Instant.now().getEpochSecond() - StartTime >= Main.data.getConfig().getInt("Users." + player.getUniqueId() + ".time")) {
                Main.data.getConfig().set("Users." + player.getUniqueId() + ".unallowed", null);
                Main.data.getConfig().set("Users." + player.getUniqueId() + ".timeUnallowed", null);
                Main.data.getConfig().set("Users." + player.getUniqueId() + ".reason", null);
                Main.data.saveConfig();
                verifyCode = getSaltString(plugin.getConfig().getInt("UnverificationCodeLength"));
                if (VerifiedCode == true && Verified == true) {
                    Main.data.getConfig().set("Users." + player.getUniqueId() + ".VerificationCode", null);
                    Main.data.saveConfig();
                }

                if (Verified == true) {
                    return;
                }

                if (Verified == false) {
                    if (VerifiedCode == true) {
                        verifyCode = Main.data.getConfig().getString("Users." + player.getUniqueId() + ".VerificationCode");
                    } else {
                        Main.data.getConfig().set("Users." + player.getUniqueId() + ".VerificationCode", verifyCode);
                        Main.data.saveConfig();
                    }
                    event.setKickMessage(ChatColor.BLUE + "You are not verified on discord!\n" + ChatColor.WHITE + "Please read the rules and verify by sending " + ChatColor.YELLOW + "SMPBot " + ChatColor.WHITE + "The following characters " + ChatColor.YELLOW + verifyCode + ChatColor.WHITE + "\nIf it " + ChatColor.RED + "fails " + ChatColor.WHITE +  "to verify you please " + ChatColor.YELLOW + "re-connect" + ChatColor.WHITE + " for a new verification code");
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, event.getKickMessage());
                }
                return;
            }
            int timeNumber = (int) (3*60 - (Instant.now().getEpochSecond() - StartTime));
            String timeString = getDurationString(timeNumber);
            event.setKickMessage(ChatColor.BLUE + "You have been unverified!\n" + ChatColor.WHITE + "Reason: " + ChatColor.YELLOW + reason + ChatColor.WHITE + "\nYou are not allowed to re verify for " + ChatColor.YELLOW + timeString + ChatColor.WHITE + "\nUse this time to read the rules again!");
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, event.getKickMessage());
            return;
        }

        if (VerifiedCode == true && Verified == true) {
            Main.data.getConfig().set("Users." + player.getUniqueId() + ".VerificationCode", null);
            Main.data.saveConfig();
        }

        if (Verified == true) {
            return;
        }

        if (Verified == false) {
            if (VerifiedCode == true) {
                verifyCode = Main.data.getConfig().getString("Users." + player.getUniqueId() + ".VerificationCode");
            } else {
                Main.data.getConfig().set("Users." + player.getUniqueId() + ".VerificationCode", verifyCode);
                Main.data.saveConfig();
            }
            event.setKickMessage(ChatColor.BLUE + "You are not verified on discord!\n" + ChatColor.WHITE + "Please read the rules and verify by sending " + ChatColor.YELLOW + "SMPBot " + ChatColor.WHITE + "The following characters " + ChatColor.YELLOW + verifyCode + ChatColor.WHITE + "\nIf it " + ChatColor.RED + "fails " + ChatColor.WHITE +  "to verify you please " + ChatColor.YELLOW + "re-connect" + ChatColor.WHITE + " for a new verification code");
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, event.getKickMessage());
        }
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if (!(event.getAuthor().isBot() == false)) {
            return;
        }
        AtomicBoolean found = new AtomicBoolean(false);
        Main.data.getConfig().getConfigurationSection("Users").getKeys(false).stream().anyMatch(key -> {
            boolean HasCode = Main.data.getConfig().isSet("Users." + key + ".VerificationCode");
            if (HasCode == true) {
                if (Main.data.getConfig().getString("Users." + key + ".VerificationCode").equalsIgnoreCase(event.getMessage().getContentDisplay())) {
                    found.set(true);
                    event.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessage("You have been successfully verified! You can now join the server")).queue();
                    Main.data.getConfig().set("Users." + key + ".Discord", event.getAuthor().getId());
                    Main.data.saveConfig();
                }
            }
            return found.get();
        });
        if (found.get() == false) {
            event.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessage("Failed to verify! Your code might be incorrect")).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        toggledGeneral = Commands.toggledGeneral;
        //toggledMessages = Commands.toggledMessages;
        toggledVisible = Commands.toggledVisible;
        if (!(event.getAuthor().isBot() == false)) {
            return;
        }
        TextChannel textChannel = event.getGuild().getTextChannelsByName("smp-chat",true).get(0);
        TextChannel generalChat = event.getGuild().getTextChannelsByName("general", true).get(0);
        if(event.getChannel() == textChannel) {
            //textChannel.sendMessage("sending message to server").queue();
            String message = event.getMessage().getContentDisplay();
            message = message.replaceAll("\n", "").replaceAll("\r", ""); //thanks mlg for making me implement a line combine
            for(Player p : Bukkit.getOnlinePlayers()){
                if (toggledGeneral.contains(p.getName())) {
                    if (!toggledVisible.contains(p.getName())) {
                        p.sendMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "SMP Chat" + ChatColor.WHITE + "] " + ChatColor.BOLD + event.getAuthor().getName() + ChatColor.RESET + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + message);
                    }
                } else {
                    if (!toggledVisible.contains(p.getName())) {
                        p.sendMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "Discord" + ChatColor.WHITE + "] " + ChatColor.BOLD + event.getAuthor().getName() + ChatColor.RESET + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + message);
                    }
                }
            }
            //Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "Discord" + ChatColor.WHITE + "] " + ChatColor.BOLD + event.getAuthor().getName() + ChatColor.RESET + ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + message);
            return;
        }
        if (event.getChannel() == generalChat) {
            String message = event.getMessage().getContentDisplay();
            message = message.replaceAll("\n", "").replaceAll("\r","");
            for(Player p : Bukkit.getOnlinePlayers()){
                if (toggledGeneral.contains(p.getName())) {
                    if (!toggledVisible.contains(p.getName())) {
                        p.sendMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "General" + ChatColor.WHITE + "] " + ChatColor.BOLD + event.getAuthor().getName() + ChatColor.RESET + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + message);
                    }
                }
            }
        }
    }
}
