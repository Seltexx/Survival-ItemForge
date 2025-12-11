package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.api.interfaces.LingoUser;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.util.CauldronManager;
import de.relaxogames.snorlaxItemForge.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TinctureFillListener implements Listener {

    private final FileManager fileManager = new FileManager();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player interacter = e.getPlayer();

        LingoUser lingoInteracter = new LingoPlayer(interacter.getUniqueId());

        // --- Schritt 1: Prüfen ob Item in der Hand ---
        ItemStack interacted = e.getItem();
        if (interacted == null)return;
        if (!interacted.getItemMeta().hasCustomModelData())return;

        // --- Schritt 2: Prüfen ob Block angeklickt wurde ---
        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null) {
            Bukkit.broadcast(Component.text("§cDEBUG: Kein Block angeklickt"));
            return;
        }

        // --- Schritt 3: Prüfen ob Cauldron ---
        if (clickedBlock.getType() != Material.CAULDRON && clickedBlock.getType() != Material.POWDER_SNOW_CAULDRON) {
            Bukkit.broadcast(Component.text("§cDEBUG: Block ist kein Cauldron"));
            return;
        }

        CauldronManager cauldron = new CauldronManager(clickedBlock, interacter, interacted);

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && interacted.getItemMeta().getCustomModelData() == 12) {
            cauldron.setLevel(cauldron.getLevel()-1);
            return;
        }

        if (interacted.getItemMeta().getCustomModelData() == 25 && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            cauldron.addAllLevel();
            return;
        }

        cauldron.update();
        ItemBuilder.updateLore(lingoInteracter.getLanguage(), interacted);
    }
}
