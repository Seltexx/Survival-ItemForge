package de.relaxogames.snorlaxItemForge;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.relaxogames.api.Lingo;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import de.relaxogames.snorlaxItemForge.listener.musicdiscs.MusicDiscs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static File datafolder;
    public static File langFolder;
    public static File advancementFolder;
    public static File discoFolder;
    public static File config;
    private static List<File> list = new ArrayList<>();

    public static void initialize(){
        datafolder = ItemForge.getForge().getDataFolder();
        langFolder = new File(datafolder + "//languages");
        advancementFolder = new File(datafolder + "//advancements");
        discoFolder = new File(datafolder + "//music");

        if (!datafolder.exists()){
            datafolder.mkdir();
        }
        if (!langFolder.exists()){
            langFolder.mkdir();
        }
        if (!advancementFolder.exists()){
            advancementFolder.mkdir();
        }
        if (!discoFolder.exists()){
            discoFolder.mkdir();
        }

        File deFile = new File(langFolder, "de_DE.yml");
        try {
            if (!deFile.exists()) {
                InputStream in = ItemForge.getForge().getResource("de_DE.yml");
                Files.copy(in, deFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        File enFile = new File(langFolder, "en_US.yml");
        try {
            if (!enFile.exists()) {
                InputStream in = ItemForge.getForge().getResource("en_US.yml");
                Files.copy(in, enFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        config = new File(datafolder, "config.yml");
        try {
            if (!config.exists()) {
                InputStream in = ItemForge.getForge().getResource("config.yml");
                Files.copy(in, config.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        list.add(deFile);
        list.add(enFile);

        loadAdvancements();
        loadSongs();
    }

    private static void loadAdvancements(){
        for (Advancement advancement : Advancement.values()){
            if (advancement.getFileName().isEmpty())continue;
            File brew_tincture = new File(advancementFolder,  advancement.getFileName() + ".json");
            try {
                if (!brew_tincture.exists()) {
                    InputStream in = ItemForge.getForge().getResource(advancement.getFileName() + ".json");
                    Files.copy(in, brew_tincture.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private static void loadSongs(){
        for (MusicDiscs music : MusicDiscs.values()){
            File nbsFile = new File(discoFolder,  music.getFile() + ".nbs");
            try {
                if (!nbsFile.exists()) {
                    InputStream in = ItemForge.getForge().getResource(music.getFile() + ".nbs");
                    Files.copy(in, nbsFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void loadMessages(Lingo lingo){
        lingo.loadMessages(list);
    }

    public int jukeboxMaxDistance(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getInt("jukebox.music-distance", 64);
    }

    public double villagerSprintingSpeed(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getDouble("npc.villager-sprinting-speed", 0.7);
    }

    public double villagerWalkingSpeed(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getDouble("npc.villager-walk-speed", 0.6);
    }

    public double villagerWorkingTableDisplayOffsetZ(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getDouble("npc.villager-station.text-display.offset-z", 0.5);
    }

    public double villagerWorkingTableDisplayOffsetY(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getDouble("npc.villager-station.text-display.offset-y", 1.25);
    }

    public double villagerWorkingTableDisplayOffsetX(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getDouble("npc.villager-station.text-display.offset-x", 0.5);
    }

    public int villagerRestockAmount(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getInt("npc.villager-amount-restock", 2);
    }

    public int villagerWorkingTableSearch(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getInt("npc.villager-search-radius", 48);
    }

    public int maxTincutureCauldronLevel(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getInt("items.invisibility-tincture.max-cauldron-level", 3);
    }

    public int maxTincutureUses(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getInt("items.invisibility-tincture.max-uses", 3);
    }

    public boolean disabledEndCrystals(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getBoolean("items.disable-end-crystal", false);
    }

    public boolean disabledTNT(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getBoolean("items.disable-tnt", false);
    }

    public boolean disabledTNTMinecart(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getBoolean("items.disable-tnt-minecart", false);
    }

    public JsonObject readJSON(Advancement advancement){
        try {
            return JsonParser.parseReader(new FileReader(new File(advancementFolder + "//" + advancement.getFileName() + ".json"))).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static File getDiscoFolder() {
        return discoFolder;
    }
}
