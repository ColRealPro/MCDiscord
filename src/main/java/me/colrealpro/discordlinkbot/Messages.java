package me.colrealpro.discordlinkbot;

import me.colrealpro.discordlinkbot.commands.Commands;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Messages extends ListenerAdapter implements Listener {
    //List<String> toggledMessages = Commands.toggledMessages;
    List<String> toggledVisible = Commands.toggledVisible;

    public static HashMap<UUID, HashMap<Long, Boolean>> toggledChannels = Main.toggledChannels;

    TextChannel channel = Main.channel;

    private final Plugin plugin = Main.getPlugin(Main.class);

    protected String getSaltString(int max) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < max) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();

    }

    private String getDurationString(int totalSecs) {

        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;

        return String.format("%02d minutes, %02d seconds", minutes, seconds);
    }

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

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        if (!plugin.getConfig().getBoolean("VerificationRequired")) return;

        Player player = event.getPlayer();
        boolean Verified = Main.data.getConfig().isSet("Users." + player.getUniqueId() + ".Discord");
        boolean VerifiedCode = Main.data.getConfig().isSet("Users." + player.getUniqueId() + ".VerificationCode");
        boolean unallowed = Main.data.getConfig().isSet("Users." + player.getUniqueId() + ".unallowed");
        String verifyCode = getSaltString(plugin.getConfig().getInt("VerificationCodeLength"));

        if (unallowed) {
            long StartTime = Main.data.getConfig().getLong("Users." + player.getUniqueId() + ".timeUnallowed");
            String reason = Main.data.getConfig().getString("Users." + player.getUniqueId() + ".reason");
            if (Instant.now().getEpochSecond() - StartTime >= Main.data.getConfig().getInt("Users." + player.getUniqueId() + ".time")) {
                Main.data.getConfig().set("Users." + player.getUniqueId() + ".unallowed", null);
                Main.data.getConfig().set("Users." + player.getUniqueId() + ".timeUnallowed", null);
                Main.data.getConfig().set("Users." + player.getUniqueId() + ".reason", null);
                Main.data.saveConfig();
                verifyCode = getSaltString(plugin.getConfig().getInt("UnverificationCodeLength"));
                if (VerifiedCode && Verified) {
                    Main.data.getConfig().set("Users." + player.getUniqueId() + ".VerificationCode", null);
                    Main.data.saveConfig();
                }

                if (Verified == true) {
                    return;
                }

                if (Verified == false) {
                    if (VerifiedCode) {
                        verifyCode = Main.data.getConfig().getString("Users." + player.getUniqueId() + ".VerificationCode");
                    } else {
                        Main.data.getConfig().set("Users." + player.getUniqueId() + ".VerificationCode", verifyCode);
                        Main.data.saveConfig();
                    }
                    event.setKickMessage(ChatColor.BLUE + "You're account is not linked with discord!\n" + ChatColor.WHITE + "Please read the rules and link you account by sending " + ChatColor.YELLOW +  Main.jda.getSelfUser().getAsTag() + " " + ChatColor.WHITE + "The following characters " + ChatColor.YELLOW + verifyCode + ChatColor.WHITE + "\nIf it " + ChatColor.RED + "fails " + ChatColor.WHITE +  "to verify you please " + ChatColor.YELLOW + "re-connect" + ChatColor.WHITE + " for a new verification code");
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, event.getKickMessage());
                }
                return;
            }
            int timeNumber = (int) (3*60 - (Instant.now().getEpochSecond() - StartTime));
            String timeString = getDurationString(timeNumber);
            event.setKickMessage(ChatColor.BLUE + "You have been unverified!\n" + ChatColor.WHITE + "Reason: " + ChatColor.YELLOW + reason + ChatColor.WHITE + "\nYou are not allowed to re link your account for " + ChatColor.YELLOW + timeString + ChatColor.WHITE + "\nUse this time to read the rules again!");
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, event.getKickMessage());
            return;
        }

        if (VerifiedCode && Verified) {
            Main.data.getConfig().set("Users." + player.getUniqueId() + ".VerificationCode", null);
            Main.data.saveConfig();
        }

        if (Verified == true) {
            return;
        }

        if (Verified == false) {
            if (VerifiedCode) {
                verifyCode = Main.data.getConfig().getString("Users." + player.getUniqueId() + ".VerificationCode");
            } else {
                Main.data.getConfig().set("Users." + player.getUniqueId() + ".VerificationCode", verifyCode);
                Main.data.saveConfig();
            }
            event.setKickMessage(ChatColor.BLUE + "You're account is not linked with discord!\n" + ChatColor.WHITE + "Please read the rules and link you account by sending " + ChatColor.YELLOW + "SMPBot " + ChatColor.WHITE + "The following characters " + ChatColor.YELLOW + verifyCode + ChatColor.WHITE + "\nIf it " + ChatColor.RED + "fails " + ChatColor.WHITE +  "to verify you please " + ChatColor.YELLOW + "re-connect" + ChatColor.WHITE + " for a new verification code");
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, event.getKickMessage());
        }
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        AtomicBoolean found = new AtomicBoolean(false);
        Main.data.getConfig().getConfigurationSection("Users").getKeys(false).stream().anyMatch(key -> {
            boolean HasCode = Main.data.getConfig().isSet("Users." + key + ".VerificationCode");
            if (HasCode == true) {
                if (Main.data.getConfig().getString("Users." + key + ".VerificationCode").equalsIgnoreCase(event.getMessage().getContentDisplay())) {
                    Player player = Bukkit.getPlayer(UUID.fromString(key));
                    found.set(true);
                    event.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessage("You have been successfully linked to the Minecraft Account: **" + player.getName() + "**")).queue();
                    Main.data.getConfig().set("Users." + key + ".Discord", event.getAuthor().getId());
                    Main.data.saveConfig();
                    if (plugin.getConfig().getBoolean("VerificationRequired") == false) {
                        if (player.isOnline() == true) {
                            player.sendMessage(ChatColor.BLUE + "You were successfully linked to the Discord Account: " + ChatColor.GRAY + event.getAuthor().getAsTag());
                        }
                    }
                }
            }
            return found.get();
        });
        if (found.get() == false) {
            event.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessage("Failed to link account! Your code might be incorrect")).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        toggledVisible = Commands.toggledVisible;
        if (!(event.getAuthor().isBot() == false)) {
            return;
        }

        String message = event.getMessage().getContentDisplay();
        message = message.replaceAll("\n", "").replaceAll("\r", " ");

        for(Player p : Bukkit.getOnlinePlayers()){
            if (toggledVisible.contains(p.getName())) continue;
            if (toggledChannels.get(p.getUniqueId()) == null) continue;
            if (toggledChannels.get(p.getUniqueId()).containsKey(event.getChannel().getIdLong()) == false && !(event.getChannel().getId().equals(channel.getId()))) continue;
            if (event.getChannel().getIdLong() == channel.getIdLong() || toggledChannels.get(p.getUniqueId()).get(event.getChannel().getIdLong()) == true) {
                p.sendMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "#" + Capital(event.getChannel().getName().replaceAll("-", " ")) + ChatColor.WHITE + "] " + ChatColor.BOLD + event.getAuthor().getName() + ChatColor.RESET + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + message);
            }
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("playerlist")) return;
        MessageBuilder playerlist = new MessageBuilder();
        Player[] list = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        if (list.length > 30) {
            event.reply("Too many players are on to show each name!\nPlayer Count: " + list.length + "/" + Bukkit.getServer().getMaxPlayers()).setEphemeral(true).queue();
            return;
        } else if (list.length == 0) {
            event.reply("**There are currently no players online!**").setEphemeral(true).queue();
        }
        playerlist.append("*Online Players:*\n");
        for (Player p : list) {
            playerlist.append(p.getName() + "\n");
        }
        playerlist.append("**There are currently " + list.length + " players online!**");
        event.reply(playerlist.build()).setEphemeral(true).queue();
    }
}
