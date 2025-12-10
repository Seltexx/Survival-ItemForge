package de.relaxogames.snorlaxItemForge.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.api.interfaces.LingoUser;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class TinctureInteractListener implements Listener {

    private final FileManager fileManager = new FileManager();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player interacter = e.getPlayer();
        LingoUser lingoInteracter = new LingoPlayer(interacter.getUniqueId());

        // --- Schritt 1: Prüfen ob Item in der Hand ---
        ItemStack interacted = e.getItem();
        if (interacted == null) {
            Bukkit.broadcast(Component.text("§cDEBUG: Kein Item in der Hand"));
            return;
        }

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
        Bukkit.broadcast(Component.text("§aDEBUG: Cauldron angeklickt"));

        // --- Schritt 4: CustomBlockData laden ---
        CustomBlockData cauldronBlock = new CustomBlockData(clickedBlock, ItemForge.getForge());

        NamespacedKey hasTinctureKey = new NamespacedKey(ItemForge.getForge(), "contains_tincture");
        boolean hasTincture = Boolean.TRUE.equals(cauldronBlock.get(hasTinctureKey, PersistentDataType.BOOLEAN));
        Bukkit.broadcast(Component.text("§eDEBUG: contains_tincture = " + hasTincture));

        NamespacedKey tinctureLevelKey = new NamespacedKey(ItemForge.getForge(), "tincture_level");
        Integer levelObj = cauldronBlock.get(tinctureLevelKey, PersistentDataType.INTEGER);
        int level = levelObj == null ? 0 : levelObj;
        Bukkit.broadcast(Component.text("§eDEBUG: Cauldron Level = " + level));

        // --- Schritt 6: Item benutzen zum Befüllen ---
        if (interacted.getItemMeta().getCustomModelData() == 25 && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Bukkit.broadcast(Component.text("§aDEBUG: Richtiges Item benutzt"));

            NamespacedKey bottleKey = new NamespacedKey(ItemForge.getForge(), "left_filling");
            Integer bottleLvlObj = interacted.getItemMeta().getPersistentDataContainer().get(bottleKey, PersistentDataType.INTEGER);
            int bottleLvl = bottleLvlObj == null ? 0 : bottleLvlObj;
            Bukkit.broadcast(Component.text("§eDEBUG: Bottle Level = " + bottleLvl));

            if (bottleLvl < 1) {
                Bukkit.broadcast(Component.text("§cDEBUG: Bottle leer → Abbruch"));
                return;
            }

            int maxLevel = fileManager.maxTincutureCauldronLevel();
            Bukkit.broadcast(Component.text("§eDEBUG: Max Cauldron Level = " + maxLevel));

            if (level >= maxLevel) {
                Bukkit.broadcast(Component.text("§cDEBUG: Cauldron ist voll"));
                return;
            }

            if (clickedBlock.getBlockData() instanceof Levelled levelled) {
                if (!hasTincture && levelled.getLevel() >= 1) return;

                int visLevel = Math.min(level + 1, 3);
                Bukkit.broadcast(Component.text("§aDEBUG: Visuelles Level gesetzt auf " + visLevel));

                levelled.setLevel(visLevel);
                clickedBlock.setBlockData(levelled);
                cauldronBlock.set(tinctureLevelKey, PersistentDataType.INTEGER, level + 1);

                interacted.getItemMeta().getPersistentDataContainer().set(bottleKey, PersistentDataType.INTEGER, bottleLvl - 1);
                Bukkit.broadcast(Component.text("§aDEBUG: Bottle Level jetzt = " + (bottleLvl - 1)));

                if (bottleLvl - 1 == 0) {
                    Bukkit.broadcast(Component.text("§cDEBUG: Item entfernt"));
                    interacter.getInventory().remove(interacted);
                }
            } else {
                Bukkit.broadcast(Component.text("§cDEBUG: BlockData ist NICHT Levelled"));
            }
            return;
        }

        // --- Schritt 5: LEEREN prüfen ---
        if (level <= 0) {
            Bukkit.broadcast(Component.text("§cDEBUG: Level <= 0 → Abbruch"));
            clickedBlock.getWorld().playSound(clickedBlock.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1f, 2f);
            cauldronBlock.set(tinctureLevelKey, PersistentDataType.INTEGER, 0);
            return;
        }
    }
}
