package me.colrealpro.discordlinkbot;

import me.colrealpro.discordlinkbot.Files.DataManager;
import me.colrealpro.discordlinkbot.Inventories.UnverifyInventories;
import me.colrealpro.discordlinkbot.commands.*;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.colrealpro.discordlinkbot.StartBot.jda;

public final class Main extends JavaPlugin {

    public static DataManager data;
    public ConsoleCommandSender console;
    public static SignMenuFactory signMenuFactory;
    public static Inventory invCreateCustom;

    @Override
    public void onEnable() {
        this.signMenuFactory = new SignMenuFactory(this);
        this.data = new DataManager(this);
        Commands commands = new Commands();
        createLanguageFiles();
        console = Bukkit.getServer().getConsoleSender();
        console.sendMessage("[DiscordLink BOT] Plugin started");
        try {
            new StartBot(this);
        } catch (LoginException e) {
            e.printStackTrace();
        }
        getCommand("show").setExecutor(commands);
        getCommand("show").setTabCompleter(new ShowTabCompletion());
        getCommand("chatlink").setExecutor(commands);
        getCommand("chatlink").setTabCompleter(new DiscordLinkTabCompletion());
        getCommand("unverify").setExecutor(commands);
        getServer().getPluginManager().registerEvents(new Messages(), this);

        //Webhook
        String webhookUrl = "https://discord.com/api/webhooks/885681755489726484/-MMbXCtRjEpdMxhojUS8yXtgK9Cr5mk018PnN180voN9VXrm3ZPJMTAVPJ_uaHyP99R8";
        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
        webhook.addEmbed(new DiscordWebhook.EmbedObject().setDescription("Successfully started the DiscordLink plugin by ColRealPro"));
        try {
            webhook.execute();
        }
        catch(java.io.IOException e) {
            getLogger().severe(e.getStackTrace().toString());
        }
        getServer().getPluginManager().registerEvents(new MessageListener(), this);
        getCommand("sendmessages").setExecutor(new ConsoleCommands());
        createInv();
        getCommand("opensigntest").setExecutor(new OpenSignTest());
        getServer().getPluginManager().registerEvents(new UnverifyInventories(), this);
    }

    public void createInv() {
        invCreateCustom = Bukkit.createInventory(null, 27, ChatColor.GRAY + "" + ChatColor.GOLD +  "Custom" + ChatColor.GRAY +  " Unverification Reason");

        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Reason
        item.setType(Material.BLUE_STAINED_GLASS_PANE);
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "The reason shown after a player is unverified");
        meta.setDisplayName(ChatColor.BLUE + "Set Reason");
        meta.setLore(lore);
        item.setItemMeta(meta);
        invCreateCustom.setItem(10, item);

        // Time
        item.setType(Material.RED_STAINED_GLASS_PANE);
        meta.setDisplayName(ChatColor.RED + "Set Time");
        lore.clear();
        lore.add(ChatColor.GRAY + "The amount of time before a player");
        lore.add(ChatColor.GRAY + "can re-verify");
        meta.setLore(lore);
        item.setItemMeta(meta);
        invCreateCustom.setItem(16, item);

        // Close Barrier
        item.setType(Material.BARRIER);
        meta.setDisplayName(ChatColor.RED + "Close");
        lore.clear();
        meta.setLore(lore);
        item.setItemMeta(meta);
        invCreateCustom.setItem(22, item);

        // Empty Slots
        item.setType(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        meta.setDisplayName(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);

        fillEmptySlots(invCreateCustom, item);
    }

    public void fillEmptySlots(Inventory inv, ItemStack item) {
        for (int i = 0; i < inv.getSize(); i++) {
            if(inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) {
                inv.setItem(i, item);
            }
        }
    }

    public void copyFileFromJarToOutside(String inputPath, String destPath){
        URL inputUrl = getClass().getResource(inputPath);
        File dest = new File(destPath);
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public SignMenuFactory getSignMenuFactory() {
        return this.signMenuFactory;
    }

    public void createLanguageFiles(){
        File file = new File(getDataFolder()+File.separator+"/config.yml");
        if (!file.exists()) {
            copyFileFromJarToOutside("/config.yml", getDataFolder()+File.separator+"/config.yml");
        }
    }

    @Override
    public void onDisable() {
        String webhookUrl = "https://discord.com/api/webhooks/885681755489726484/-MMbXCtRjEpdMxhojUS8yXtgK9Cr5mk018PnN180voN9VXrm3ZPJMTAVPJ_uaHyP99R8";
        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
        webhook.addEmbed(new DiscordWebhook.EmbedObject().setDescription("Server Closed"));
        try {
            webhook.execute();
        }
        catch(java.io.IOException e) {
            getLogger().severe(e.getStackTrace().toString());
        }
        if (jda != null)
            jda.shutdownNow();
    }

    /*public static JDA jda;

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createDefault("ODM3NDE4NzU4OTU5NzkyMjI4.YIsQ_g.fn-CaaUMO7raGrjmD3IyMYFtk4M").build();
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.watching("ColSMP"));

        jda.addEventListener(new Messages());
    }*/
}
