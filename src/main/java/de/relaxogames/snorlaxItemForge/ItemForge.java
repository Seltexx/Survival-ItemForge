package de.relaxogames.snorlaxItemForge;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemForge extends JavaPlugin {

    private static ItemForge instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        commandRegis();
        listenerRegis();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void commandRegis(){

    }

    private void listenerRegis(){
        PluginManager pm = Bukkit.getPluginManager();
    }

    public static ItemForge getForge() {
        return instance;
    }
}
