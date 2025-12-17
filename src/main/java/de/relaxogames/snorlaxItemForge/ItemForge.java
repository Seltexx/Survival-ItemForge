package de.relaxogames.snorlaxItemForge;

import de.relaxogames.api.Lingo;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import de.relaxogames.snorlaxItemForge.commands.TestCommand;
import de.relaxogames.snorlaxItemForge.listener.*;
import de.relaxogames.snorlaxItemForge.listener.villager.BeekeeperListener;
import de.relaxogames.snorlaxItemForge.listener.villager.VillagerListener;
import de.relaxogames.snorlaxItemForge.listener.villager.WorkingStationBreak;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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
        FileManager.initialize(); // Load all files
        lingo = new Lingo(getDataFolder());
        fileManager.loadMessages(lingo);
        Advancements.loadAll();
        VillagerWrapper.startWorkClock();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void commandRegis(){
        getCommand("debug").setExecutor(new TestCommand());
    }

    private void listenerRegis(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BrewListener(), this);
        pm.registerEvents(new CauldronListener(), this);
        pm.registerEvents(new DisableListener(), this);
        pm.registerEvents(new ItemFrameConvert(), this);
        pm.registerEvents(new ItemFrameListener(), this);
        pm.registerEvents(new PebbleSnowballListener(), this);
        pm.registerEvents(new EnderDragonEggListener(), this);

        pm.registerEvents(new VillagerListener(), this);
        pm.registerEvents(new BeekeeperListener(), this);
        pm.registerEvents(new WorkingStationBreak(), this);
    }

    public static @NotNull Plugin getForge() {
        return instance;
    }

    public static Lingo getLingo() {
        return lingo;
    }

    public static void setLingo(Lingo lingo) {
        ItemForge.lingo = lingo;
    }
}
