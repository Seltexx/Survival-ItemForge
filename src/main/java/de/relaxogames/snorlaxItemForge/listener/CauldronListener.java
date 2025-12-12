package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.snorlaxItemForge.util.CauldronManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CauldronListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player interacter = e.getPlayer();

        // --- Schritt 1: Prüfen ob Item in der Hand ---
        ItemStack interacted = e.getItem();
        if (interacted == null)return;
        if (interacted.getItemMeta() == null)return;
        if (!interacted.getItemMeta().hasCustomModelData())return;

        // --- Schritt 2: Prüfen ob Block angeklickt wurde ---
        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null)return;

        // --- Schritt 3: Prüfen ob Cauldron ---
        if (clickedBlock.getType() != Material.CAULDRON && clickedBlock.getType() != Material.POWDER_SNOW_CAULDRON)return;

        CauldronManager cauldron = new CauldronManager(clickedBlock, interacter, interacted);

        //TODO: MILK BUKKET GEBEN
        if (cauldron.containsTincture() && interacted.getType().equals(Material.BUCKET)){
            e.setCancelled(true);
            return;
        }

        if (interacted.getItemMeta().getCustomModelData() != 25)return;

        if (interacted.getItemMeta().getCustomModelData() == 25 && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            cauldron.addAllLevel();
            return;
        }
        cauldron.update();
        cauldron.updateMainHand();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Player breaker = e.getPlayer();

        Block brokenBlock = e.getBlock();
        if (brokenBlock == null || !brokenBlock.getType().equals(Material.POWDER_SNOW_CAULDRON))return;
        CauldronManager cauldron = new CauldronManager(brokenBlock, breaker);
        if (!cauldron.hasData())return;

        cauldron.delete();
    }

    @EventHandler
    public void bukkitFill(PlayerBucketFillEvent e){
        Player bucketOwner = e.getPlayer();

        Block block = e.getBlockClicked();
        if (block == null || !block.getType().equals(Material.POWDER_SNOW_CAULDRON))return;
        CauldronManager cauldron = new CauldronManager(block, bucketOwner);

        if (!cauldron.hasData())return;
        if (!cauldron.containsTincture())return;
        e.setCancelled(true);
        block.getWorld().playSound(block.getLocation(), Sound.ITEM_SHIELD_BREAK, 1000, 0);
    }

    @EventHandler
    public void bottleFill(PlayerInteractEvent e) {
        Player bucketOwner = e.getPlayer();

        ItemStack itemInHand = e.getItem();
        if (itemInHand == null || (!itemInHand.getType().equals(Material.GLASS_BOTTLE) && !itemInHand.getType().equals(Material.POWDER_SNOW_BUCKET))) return;
        if (itemInHand.getItemMeta() == null) return;

        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Block block = e.getClickedBlock();
        if (block == null || !block.getType().equals(Material.POWDER_SNOW_CAULDRON)) return;
        CauldronManager cauldron = new CauldronManager(block, bucketOwner);

        if (!cauldron.hasData()) return;
        if (!cauldron.containsTincture()) return;
        e.setCancelled(true);
        block.getWorld().playSound(block.getLocation(), Sound.ITEM_OMINOUS_BOTTLE_DISPOSE, 1000, 0);
    }
}
