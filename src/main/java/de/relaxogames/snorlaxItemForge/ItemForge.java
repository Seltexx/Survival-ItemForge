package de.relaxogames.snorlaxItemForge;

import de.relaxogames.api.Lingo;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import de.relaxogames.snorlaxItemForge.commands.Debug;
import de.relaxogames.snorlaxItemForge.listener.BrewListener;
import de.relaxogames.snorlaxItemForge.listener.TinctureInteractListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemForge extends JavaPlugin {

    private static ItemForge instance;
    private static Lingo lingo;

    private FileManager fileManager = new FileManager();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        commandRegis();
        listenerRegis();
        lingo = new Lingo(getDataFolder());
        FileManager.initialize(); // Load all files
        fileManager.loadMessages(lingo);
        Advancements.loadAll();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void commandRegis(){
        getCommand("debug").setExecutor(new Debug());
    }

    private void listenerRegis(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BrewListener(), this);
        pm.registerEvents(new TinctureInteractListener(), this);
    }

    public static ItemForge getForge() {
        return instance;
    }

    public static Lingo getLingo() {
        return lingo;
    }

    public static void setLingo(Lingo lingo) {
        ItemForge.lingo = lingo;
    }
}
