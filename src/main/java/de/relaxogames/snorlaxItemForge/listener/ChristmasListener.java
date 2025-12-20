package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.snorlaxItemForge.ChristmasItems;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ChristmasListener implements Listener {

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() == Material.AIR || !item.hasItemMeta()) return;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(ChristmasItems.CHRISTMAS_ITEM_KEY, PersistentDataType.INTEGER)) return;

        int type = pdc.get(ChristmasItems.CHRISTMAS_ITEM_KEY, PersistentDataType.INTEGER);
        Player player = event.getPlayer();

        switch (type) {
            case 102: // Baked Apple
                player.setFreezeTicks(0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 30, 0));
                Advancements.playout(player, Advancement.CHRISTMAS_COOK);
                break;
            case 103: // Hot Chocolate
                player.setFreezeTicks(0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 5, 0));
                Advancements.playout(player, Advancement.CHRISTMAS_DRINKS);
                break;
            case 104: // Glühwein
                player.setFreezeTicks(0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 60 * 3, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 10, 0));
                Advancements.playout(player, Advancement.CHRISTMAS_DRINKS);
                break;
        }
    }
}
