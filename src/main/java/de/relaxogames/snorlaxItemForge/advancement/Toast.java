package de.relaxogames.snorlaxItemForge.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public final class Toast {

    private FileManager fileManager = new FileManager();

    private final Player player;
    private Advancement advancement;
    private final NamespacedKey key;
    private final String icon;
    private final String message;
    private final Style style;
    private Toast(Player player, NamespacedKey key, Advancement advancement, String icon, String message, Style style) {
        this.player = player;
        this.icon = icon;
        this.key = key;
        this.message = message;
        this.style = style;
        this.advancement = advancement;
    }

    public void kickoff(){
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
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria("brewed_tincture");
    }

    private void revokeAdvancement(Player player){
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).revokeCriteria("brewed_tincture");
    }

    public static void displayTo(Player player, Advancement advancement, String icon, String message, Style style){
        new Toast(player, new NamespacedKey(ItemForge.getForge(), Advancement.BREWED_TINCTURE.fileName), advancement,  icon, message, style).kickoff();
    }

    public static void displayTo(Player player, Advancement advancement, String icon, String message, String trigger, Style style){
        new Toast(player, new NamespacedKey(ItemForge.getForge(), Advancement.BREWED_TINCTURE.fileName), advancement,  icon, message, style).kickoff();
    }

    public static Toast create(Player player, Advancement advancement, String icon, String message, Style style){
        return new Toast(player, new NamespacedKey(ItemForge.getForge(), Advancement.BREWED_TINCTURE.fileName), advancement,  icon, message, style);
    }

    public static enum Style{
        GOAL,
        TASK,
        CHALLENGE;
    }

}
