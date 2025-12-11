package de.relaxogames.snorlaxItemForge;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.relaxogames.api.Lingo;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
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
    public static File config;
    private static List<File> list = new ArrayList<>();

    public static void initialize(){
        datafolder = ItemForge.getForge().getDataFolder();
        langFolder = new File(datafolder + "//languages");
        advancementFolder = new File(datafolder + "//advancements");

        if (!datafolder.exists()){
            datafolder.mkdir();
        }
        if (!langFolder.exists()){
            langFolder.mkdir();
        }
        if (!advancementFolder.exists()){
            advancementFolder.mkdir();
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
    }

    private static void loadAdvancements(){
        for (Advancement advancement : Advancement.values()){
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

    public void loadMessages(Lingo lingo){
        lingo.loadMessages(list);
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
}
