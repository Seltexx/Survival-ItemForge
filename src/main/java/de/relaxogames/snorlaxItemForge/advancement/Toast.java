package de.relaxogames.snorlaxItemForge.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class Toast {

    private FileManager fileManager = new FileManager();

    private Advancement advancement;
    private final NamespacedKey key;
    private final String icon;
    private final String message;
    private final Style style;

    public Toast(NamespacedKey key, Advancement advancement, String icon, String message, Style style) {
        this.icon = icon;
        this.key = key;
        this.message = message;
        this.style = style;
        this.advancement = advancement;
    }

    private void start(Player player){
        createAdvancement();
        grantAdvancement(player);

        Bukkit.getScheduler().runTaskLaterAsynchronously(ItemForge.getForge(), () ->{
            revokeAdvancement(player);
        }, 10*20);
    }

    private void createAdvancement() {
        JsonObject json = fileManager.readJSON(advancement);

        String fixed = json.toString()
                .replace("<TITLE>", message)
                .replace("<ICON>", icon)
                .replace("<FRAME>", style.name().toLowerCase());

        JsonObject finalJson = JsonParser.parseString(fixed).getAsJsonObject();

        Bukkit.getUnsafe().loadAdvancement(key, finalJson.toString());
    }

    private void grantAdvancement(Player player){
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria("get_diamond");
    }

    private void revokeAdvancement(Player player){
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).revokeCriteria("get_diamond");
    }

    public static void displayTo(Player player, Advancement advancement, String icon, String message, Style style){
        new Toast(new NamespacedKey(ItemForge.getForge(), UUID.randomUUID().toString()), advancement,  icon, message, style).start(player);
    }

    public static enum Style{
        GOAL,
        TASK,
        CHALLENGE;
    }

}
