package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.snorlaxItemForge.util.CauldronManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CauldronListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player interacter = e.getPlayer();

        // --- Schritt 1: Prüfen ob Item in der Hand ---
        ItemStack interacted = e.getItem();
        if (interacted == null)return;
        if (!interacted.getItemMeta().hasCustomModelData())return;
        if (interacted.getItemMeta().getCustomModelData() != 25)return;

        // --- Schritt 2: Prüfen ob Block angeklickt wurde ---
        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null)return;

        // --- Schritt 3: Prüfen ob Cauldron ---
        if (clickedBlock.getType() != Material.CAULDRON && clickedBlock.getType() != Material.POWDER_SNOW_CAULDRON)return;

        CauldronManager cauldron = new CauldronManager(clickedBlock, interacter, interacted);

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
}
