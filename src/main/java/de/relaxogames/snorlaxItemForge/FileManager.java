package de.relaxogames.snorlaxItemForge;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.relaxogames.api.Lingo;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
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
        File brew_tincture = new File(advancementFolder, "advancement_is_this_vape_liquid.json");
        try {
            if (!brew_tincture.exists()) {
                InputStream in = ItemForge.getForge().getResource("advancement_is_this_vape_liquid.json");
                Files.copy(in, brew_tincture.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        File tincture_explodes = new File(advancementFolder, "advancement_tincture_explosion.json");
        try {
            if (!tincture_explodes.exists()) {
                InputStream in = ItemForge.getForge().getResource("advancement_tincture_explosion.json");
                Files.copy(in, tincture_explodes.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void loadMessages(Lingo lingo){
        lingo.loadMessages(list);
    }


    public int maxTincutureUses(){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
        return fc.getInt("items.invisibility-tincture.max-uses");
    }

    public JsonObject readJSON(Advancement advancement){
        try {
            return JsonParser.parseReader(new FileReader(new File(advancementFolder + "//" + advancement.getFileName() + ".json"))).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
