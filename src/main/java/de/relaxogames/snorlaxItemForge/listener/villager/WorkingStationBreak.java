package de.relaxogames.snorlaxItemForge.listener.villager;

import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class WorkingStationBreak implements Listener {
    private final NamespacedKey BLOCK_BLOCKED_BY = new NamespacedKey(ItemForge.getForge(), "villager_uuid");
    private final NamespacedKey WORKING_TABLE_KEY = new NamespacedKey(ItemForge.getForge(), "working_station");

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Block brokenBlock = e.getBlock();

        if (brokenBlock == null)return;
        CustomBlockData blockData = new CustomBlockData(brokenBlock, ItemForge.getForge());
        System.out.println("1.1");
        if (!blockData.has(BLOCK_BLOCKED_BY))return;
        System.out.println("1.2");
        UUID uuid = UUID.fromString(blockData.get(BLOCK_BLOCKED_BY, PersistentDataType.STRING));
        Entity tableOwner = Bukkit.getEntity(uuid);

        blockData.remove(BLOCK_BLOCKED_BY);
        if (tableOwner == null || tableOwner.isDead())return;
        System.out.println("1.3");
        if (!(tableOwner instanceof Villager owner))return;
        System.out.println("1.4");
        CustomVillager customVillager = new CustomVillager(owner);

        //if (customVillager.getWorkstationLocation() == null)return;
        Bukkit.broadcast(Component.text("REMOVED: " + owner.getUniqueId() + " BEI " + customVillager.getWorkstationLocation().toString()));
        customVillager.removeWorkingstation();
    }
}
