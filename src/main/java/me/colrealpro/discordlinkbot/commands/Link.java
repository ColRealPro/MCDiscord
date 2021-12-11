package me.colrealpro.discordlinkbot.commands;

import me.colrealpro.discordlinkbot.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

import java.time.Instant;
import java.util.Random;

public class Link implements CommandExecutor {

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("link")) {
            Player player = (Player) sender;

            boolean Verified = Main.data.getConfig().isSet("Users." + player.getUniqueId() + ".Discord");
            boolean VerifiedCode = Main.data.getConfig().isSet("Users." + player.getUniqueId() + ".VerificationCode");
            boolean unallowed = Main.data.getConfig().isSet("Users." + player.getUniqueId() + ".unallowed");
            String verifyCode = getSaltString(plugin.getConfig().getInt("VerificationCodeLength"));

            if (VerifiedCode == true) {
                verifyCode = Main.data.getConfig().getString("Users." + player.getUniqueId() + ".VerificationCode");
            } else {
                Main.data.getConfig().set("Users." + player.getUniqueId() + ".VerificationCode", verifyCode);
                Main.data.saveConfig();
            }

            // Verify Message
            BaseComponent verifyMessage = new TextComponent("");

            TextComponent Line1 = new TextComponent("Please DM the following code to SMPBot:\n");
            Line1.setColor(ChatColor.BLUE);
            TextComponent code = new TextComponent(verifyCode);
            code.setColor(ChatColor.YELLOW);
            code.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "Copied"));
            code.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click To Copy").create()));

            verifyMessage.addExtra(Line1);
            verifyMessage.addExtra(code);

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
                        return true;
                    }

                    if (Verified == false) {
                        // Send Verify Message
                        player.spigot().sendMessage(verifyMessage);
                    }
                    return true;
                }
                int timeNumber = (int) (3*60 - (Instant.now().getEpochSecond() - StartTime));
                String timeString = getDurationString(timeNumber);
                // Create Unverify Message
                BaseComponent unverifyMessage = new TextComponent("");

                TextComponent unverifyLine1 = new TextComponent("You are not allowed to link your account for\n");
                unverifyLine1.setColor(ChatColor.BLUE);
                TextComponent ReasonMessage = new TextComponent("\nReason: ");
                ReasonMessage.setColor(ChatColor.YELLOW);
                TextComponent Reason = new TextComponent(reason);
                Reason.setColor(ChatColor.GRAY);
                TextComponent time = new TextComponent(timeString);
                time.setColor(ChatColor.YELLOW);

                unverifyMessage.addExtra(unverifyLine1);
                unverifyMessage.addExtra(time);
                unverifyMessage.addExtra(ReasonMessage);
                unverifyMessage.addExtra(Reason);
                // Send Message
                player.spigot().sendMessage(unverifyMessage);
                return true;
            }

            if (VerifiedCode == true && Verified == true) {
                Main.data.getConfig().set("Users." + player.getUniqueId() + ".VerificationCode", null);
                Main.data.saveConfig();
            }

            if (Verified == true) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "(!) " + ChatColor.RESET + ChatColor.WHITE + "Your account is already linked with Discord");
            }

            if (Verified == false) {
                // Send Verify Message
                player.spigot().sendMessage(verifyMessage);
            }
            return true;
        }
        return false;
    }
}
