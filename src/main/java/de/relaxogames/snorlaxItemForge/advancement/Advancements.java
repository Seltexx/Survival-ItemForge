package de.relaxogames.snorlaxItemForge.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class Advancements {

    private FileManager fileManager = new FileManager();
    private final Player player;
    private Advancement advancement;
    private NamespacedKey key;

    public Advancements(Player player, Advancement advancement) {
        this.player = player;
        this.advancement = advancement;
        this.key = new NamespacedKey(ItemForge.getForge(), advancement.getFileName());
        kickoff();
    }

    public void kickoff(){
        createAdvancement();
        grantAdvancement(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(ItemForge.getForge(), () ->{
            revokeAdvancement(player);}, 10*20);
    }

    private void createAdvancement() {
        JsonObject json = fileManager.readJSON(advancement);
        String fixed = json.toString();
        JsonObject finalJson = JsonParser.parseString(fixed).getAsJsonObject();
        Bukkit.getUnsafe().loadAdvancement(key, finalJson.toString());
    }
    private void grantAdvancement(Player player){
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria(advancement.getTrigger());
    }
    private void revokeAdvancement(Player player){
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).revokeCriteria(advancement.getTrigger());
    }

    public static Advancements playout(Player player, Advancement advancement){
        return new Advancements(player, advancement);
    }
}