package me.colrealpro.discordlinkbot;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.event.Listener;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;

public class StartBot extends ListenerAdapter implements Listener {
    public Main plugin;
    public static JDA jda;

    public StartBot(Main main) throws LoginException {
        this.plugin = main;
        startDiscordBot();
    }

    private void startDiscordBot() throws LoginException {
        try {
            jda = JDABuilder.createDefault("ODM3NDE4NzU4OTU5NzkyMjI4.YIsQ_g.fn-CaaUMO7raGrjmD3IyMYFtk4M").build().awaitReady();
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();
        }
        if (jda == null) {
            plugin.getLogger().log(Level.SEVERE, "Unable to login to bot!");
            plugin.getServer().getPluginManager().disablePlugin(new Main());
        }
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.watching("ColSMP"));

        jda.addEventListener(new Messages());
    }

    /*public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createDefault("ODM3NDE4NzU4OTU5NzkyMjI4.YIsQ_g.fn-CaaUMO7raGrjmD3IyMYFtk4M").build();
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.watching("ColSMP"));

        jda.addEventListener(new Messages());
    }*/
}
