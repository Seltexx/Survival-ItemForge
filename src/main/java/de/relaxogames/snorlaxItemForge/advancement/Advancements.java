package de.relaxogames.snorlaxItemForge.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Advancements {

    private static final FileManager fileManager = new FileManager();
    private static final Map<NamespacedKey, Boolean> loaded = new HashMap<>();

    private final Player player;
    private final Advancement advancement;
    private final NamespacedKey key;

    public Advancements(Player player, Advancement advancement) {
        this.player = player;
        this.advancement = advancement;
        this.key = new NamespacedKey(ItemForge.getForge(), advancement.getFileName());
        kickoff();
    }

    public void kickoff() {
        grantAdvancement(player);
    }

    public static void loadAll() {

        for (Advancement advancement : Advancement.values()) {
            if (advancement.getFileName().isEmpty())continue;
            NamespacedKey key = new NamespacedKey(
                    ItemForge.getForge(),
                    advancement.getFileName()
            );

            // ✅ NICHT doppelt laden
            if (Bukkit.getAdvancement(key) != null) continue;

            JsonObject json = fileManager.readJSON(advancement);
            if (json == null) {
                Bukkit.getLogger().warning("Could not find advancement file for " + key);
                continue;
            }

            try {
                Bukkit.getUnsafe().loadAdvancement(key, json.toString());
            } catch (Exception e) {
                Bukkit.getLogger().severe("Failed to load advancement " + key + ": " + e.getMessage());
            }
        }
    }

    private void grantAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key))
                .awardCriteria(advancement.getTrigger());
    }

    private void revokeAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key))
                .revokeCriteria(advancement.getTrigger());
    }

    public static Advancements playout(Player player, Advancement advancement) {
        return new Advancements(player, advancement);
    }

    public static void playout(Player player, Advancement advancement, String criteria){
        NamespacedKey key = new NamespacedKey(ItemForge.getForge(), advancement.getFileName());
        player.getAdvancementProgress(Bukkit.getAdvancement(key))
                .awardCriteria(criteria);
    }
}
