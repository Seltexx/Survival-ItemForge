package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.languages.ServerColors;
import de.relaxogames.snorlaxItemForge.ItemForge;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;

import java.util.logging.Level;

public class ItemFrameListener implements Listener {

    private final NamespacedKey INVIS_FRAME = new NamespacedKey(ItemForge.getForge(), "isInvisFrame");

    @EventHandler
    public void onItemSwitchToFrame(PlayerItemHeldEvent e) {
        Player itemOwner = e.getPlayer();
        double corX = itemOwner.getX();
        double corY = itemOwner.getY();
        double corZ = itemOwner.getZ();

        ItemStack nowHolding = itemOwner.getInventory().getItem(e.getNewSlot());

        if (nowHolding == null || nowHolding.getItemMeta() == null) return;
        if (!nowHolding.getItemMeta().hasCustomModelData() || nowHolding.getItemMeta().getCustomModelData() != 35)
            return;
        for (Entity entitiesNearby : itemOwner.getLocation().getNearbyEntities(corX, corY, corZ)) {
            if (!(entitiesNearby instanceof ItemFrame frame)) continue;
            boolean isInvisFrame = frame.getPersistentDataContainer().get(INVIS_FRAME, PersistentDataType.BOOLEAN) == null ? false : frame.getPersistentDataContainer().get(INVIS_FRAME, PersistentDataType.BOOLEAN);
            if (isInvisFrame) frame.setVisible(true);
        }
    }

    @EventHandler
    public void onItemSwitchFromFrame(PlayerItemHeldEvent e) {
        Player itemOwner = e.getPlayer();
        double corX = itemOwner.getX();
        double corY = itemOwner.getY();
        double corZ = itemOwner.getZ();

        ItemStack previousHolding = itemOwner.getInventory().getItem(e.getPreviousSlot());
        if (previousHolding == null || previousHolding.getItemMeta() == null) return;
        if (!previousHolding.getItemMeta().hasCustomModelData()) return;
        if (previousHolding.getItemMeta().getCustomModelData() != 35) return;
        for (Entity entitiesNearby : itemOwner.getLocation().getNearbyEntities(corX, corY, corZ)) {
            if (!(entitiesNearby instanceof ItemFrame frame)) continue;
            Boolean isInvisibleFrame = frame.getPersistentDataContainer().get(INVIS_FRAME, PersistentDataType.BOOLEAN);
            if (Boolean.TRUE.equals(isInvisibleFrame)) frame.setVisible(false);
        }
    }

    @EventHandler
    public void onFrameItemRemove(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof ItemFrame frame)) return;
        ItemStack item = frame.getItem();
        if (item.getType() == Material.AIR) return;

        Boolean isInvisibleFrame = frame.getPersistentDataContainer().get(INVIS_FRAME, PersistentDataType.BOOLEAN);
        if (Boolean.TRUE.equals(isInvisibleFrame)) {
            checkUnusedFrames(frame);
        }
    }


    @EventHandler
    public void onPreparePlace(HangingPlaceEvent e) {
        Player placer = e.getPlayer();

        ItemStack itemInHand = e.getItemStack();

        if (itemInHand == null || (!itemInHand.getType().equals(Material.GLOW_ITEM_FRAME) && !itemInHand.getType().equals(Material.ITEM_FRAME)))
            return;
        if (itemInHand.getItemMeta() == null) return;
        if (!itemInHand.getItemMeta().hasCustomModelData()) return;
        if (itemInHand.getItemMeta().getCustomModelData() != 35) return;

        if (!(e.getEntity() instanceof ItemFrame placedFrame)) return;
        placedFrame.setVisible(false);
        placedFrame.getPersistentDataContainer().set(INVIS_FRAME, PersistentDataType.BOOLEAN, true);

        checkUnusedFrames(placedFrame);
    }

    private void checkUnusedFrames(ItemFrame frame) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(ItemForge.getForge(), new Runnable() {
            @Override
            public void run() {
                if (frame.getItem() == null || frame.getItem().getType().equals(Material.AIR)) {
                    Bukkit.getScheduler().runTask(ItemForge.getForge(), frame::remove);
                    ItemForge.getForge().getLogger().log(Level.INFO, "ItemFrame [x:"
                            + frame.getLocation().getBlock().getX() +
                            " / y: " + frame.getLocation().getBlock().getY() +
                            " / z: " + frame.getLocation().getBlock().getZ() + "] wurde aufgrund von Inaktivität entfernt!");
                }
            }
        }, 20 * 5);
    }
}
