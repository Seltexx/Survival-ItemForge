package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.snorlaxItemForge.ChristmasItems;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
            case 101: // Unbaked Apple
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 5, 0));
                spawnChristmasParticles(player);
                break;
            case 102: // Baked Apple
                player.setFreezeTicks(0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 30, 0));
                Advancements.playout(player, Advancement.CHRISTMAS_COOK);
                spawnChristmasParticles(player);
                break;
            case 103: // Hot Chocolate
                player.setFreezeTicks(0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 5, 0));
                Advancements.playout(player, Advancement.CHRISTMAS_DRINKS);
                spawnChristmasParticles(player);
                break;
            case 104: // Glühwein
                player.setFreezeTicks(0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 60 * 3, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 10, 0));
                Advancements.playout(player, Advancement.CHRISTMAS_DRINKS);
                spawnChristmasParticles(player);
                break;
            case 105: // Gingerbread Man
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 30, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 0));
                Advancements.playout(player, Advancement.CHRISTMAS_SWEETS);
                spawnChristmasParticles(player);
                break;
            case 106: // Candy Cane
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20 * 60, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 20 * 60, 0));
                Advancements.playout(player, Advancement.CHRISTMAS_SWEETS);
                spawnChristmasParticles(player);
                break;
            case 107: // Christmas Pudding
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 10, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 20, 0));
                Advancements.playout(player, Advancement.CHRISTMAS_SWEETS);
                spawnChristmasParticles(player);
                break;
            case 108: // Roasted Almonds
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 60, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 60, 0));
                Advancements.playout(player, Advancement.CHRISTMAS_SWEETS);
                spawnChristmasParticles(player);
                break;
            case 109: // Unbaked Gingerbread
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 5, 0));
                spawnChristmasParticles(player);
                break;
            case 110: // Sweetened Almonds
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 2, 0));
                spawnChristmasParticles(player);
                break;
            case 113: // Unmixed Pudding
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 5, 0));
                spawnChristmasParticles(player);
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.PLAYER_HEAD || !item.hasItemMeta()) return;

        if (item.getItemMeta().getPersistentDataContainer().has(ChristmasItems.CHRISTMAS_ITEM_KEY, PersistentDataType.INTEGER)) {
            // Wenn der Spieler nicht schleicht, blockieren wir die Interaktion mit dem Block,
            // damit das Essen (Item-Nutzung) Priorität hat.
            if (!event.getPlayer().isSneaking()) {
                event.setUseInteractedBlock(Event.Result.DENY);
                // In 1.21.1, explicitly allowing UseItemInHand can help the client prioritize food
                event.setUseItemInHand(Event.Result.ALLOW);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() != Material.PLAYER_HEAD || !item.hasItemMeta()) return;

        if (item.getItemMeta().getPersistentDataContainer().has(ChristmasItems.CHRISTMAS_ITEM_KEY, PersistentDataType.INTEGER)) {
            event.setCancelled(true);
        }
    }

    private void spawnChristmasParticles(Player player) {
        player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.05);
        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0.05);
    }
}
