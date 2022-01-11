package me.colrealpro.discordlinkbot;

import me.colrealpro.discordlinkbot.Files.DataManager;
import me.colrealpro.discordlinkbot.Files.messagesDataManager;
import me.colrealpro.discordlinkbot.Inventories.UnverifyInventories;
import me.colrealpro.discordlinkbot.commands.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;


public final class Main extends JavaPlugin {

    public static DataManager data;
    public static messagesDataManager messagesData;
    public ConsoleCommandSender console;
    public static SignMenuFactory signMenuFactory;
    public static Guild guild;
    public static TextChannel channel;
    public static JDA jda;

    public static HashMap<UUID, HashMap<Long, Boolean>> toggledChannels = new HashMap<>();

    @Override
    public void onEnable() {
        this.signMenuFactory = new SignMenuFactory(this);
        this.data = new DataManager(this);
        this.messagesData = new messagesDataManager(this);
        createLanguageFiles();
        console = Bukkit.getServer().getConsoleSender();
        console.sendMessage("[DiscordLink BOT] Plugin started");
        //Start Bot
        try {
            jda = JDABuilder.createDefault(getConfig().getString("BotToken")).build().awaitReady();
        } catch (InterruptedException | LoginException e) {
            getLogger().log(Level.SEVERE, "Error occurred while logging into bot!");
        }
        if (jda == null) {
            getLogger().log(Level.SEVERE, "Unable to login to bot! Disabling Plugin");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.watching("Servers"));

        //Get Discord Server Info
        guild = jda.getGuildById(getConfig().getLong("GuildID"));
        if (getConfig().getBoolean("UseChannelId")) {
            channel = guild.getTextChannelById(getConfig().getLong("MessagesChannel"));
        } else {
            channel = guild.getTextChannelsByName(getConfig().getString("MessagesChannel"), true).get(0);
        }

        if (guild == null || channel == null) {
            getServer().getPluginManager().disablePlugin(this);
        }

        // Load all player info
        this.loadData();

        Commands commands = new Commands();

        jda.upsertCommand("playerlist", "Gets a list of all the current players online! Max: 30").queue();
        jda.addEventListener(new Messages());

        getCommand("show").setExecutor(commands);
        getCommand("show").setTabCompleter(new ShowTabCompletion());
        getCommand("chatlink").setExecutor(commands);
        getCommand("chatlink").setTabCompleter(new DiscordLinkTabCompletion());
        getCommand("unverify").setExecutor(commands);
        getServer().getPluginManager().registerEvents(new Messages(), this);

        //Send Start Message
        EmbedBuilder StartEmbed = new EmbedBuilder();

        StartEmbed.setDescription("MCDiscord Loaded! Server Starting");

        channel.sendMessage(StartEmbed.build()).queue();

        getServer().getPluginManager().registerEvents(new MessageListener(), this);
        getCommand("sendmessages").setExecutor(new ConsoleCommands());
        getCommand("opensigntest").setExecutor(new OpenSignTest());
        getCommand("link").setExecutor(new Link());
        getCommand("unlink").setExecutor(new Link());
        getServer().getPluginManager().registerEvents(new UnverifyInventories(), this);
        getCommand("errorinfo").setExecutor(new errorinfo());
    }

    public void copyFileFromJarToOutside(String inputPath, String destPath){
        URL inputUrl = getClass().getResource(inputPath);
        File dest = new File(destPath);
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException e) {
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

    public void saveData() {
        for (Map.Entry<UUID, HashMap<Long, Boolean>> entry : toggledChannels.entrySet()) {
            Map<Long, Boolean> channels = toggledChannels.get(entry.getKey());
            for (Map.Entry<Long, Boolean> entry2 : channels.entrySet()) {
                data.getConfig().set("Users." + entry.getKey() + ".channels." + entry2.getKey(), entry2.getValue());
            }
        }
        data.saveConfig();
    }

    public void loadData() {
        if (data.getConfig().getConfigurationSection("Users") == null) return;
        data.getConfig().getConfigurationSection("Users").getKeys(false).forEach(key ->{

            HashMap<Long, Boolean> playerData = new HashMap<>();

            if (!data.getConfig().isSet("Users." + key + ".channels")) return;

            data.getConfig().getConfigurationSection("Users." + key + ".channels").getKeys(false).forEach(keyId ->{
                boolean value = data.getConfig().getBoolean("Users." + key + ".channels." + keyId);
                Long id = Long.parseLong(keyId);
                playerData.put(id, value);
            });

            toggledChannels.put(UUID.fromString(key), playerData);
        });
    }

    @Override
    public void onDisable() {

        //Save all player data
        this.saveData();

        if (jda != null) {
            //Send Shutdown Message

            EmbedBuilder StopEmbed = new EmbedBuilder();

            StopEmbed.setDescription("MCDiscord Disabling! Server Stopping");

            channel.sendMessage(StopEmbed.build()).complete();

            jda.shutdownNow();
        } else {
            return;
        }
    }
}
