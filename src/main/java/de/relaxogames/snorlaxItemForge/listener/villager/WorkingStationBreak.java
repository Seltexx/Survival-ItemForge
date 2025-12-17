package de.relaxogames.snorlaxItemForge.listener.villager;

import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
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

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Block brokenBlock = e.getBlock();

        if (brokenBlock == null)return;
        CustomBlockData blockData = new CustomBlockData(brokenBlock, ItemForge.getForge());
        if (!blockData.has(BLOCK_BLOCKED_BY))return;
        UUID uuid = UUID.fromString(blockData.get(BLOCK_BLOCKED_BY, PersistentDataType.STRING));
        Entity tableOwner = Bukkit.getEntity(uuid);

        blockData.remove(BLOCK_BLOCKED_BY);
        if (tableOwner == null || tableOwner.isDead())return;
        if (!(tableOwner instanceof Villager owner))return;
        CustomVillager customVillager = VillagerWrapper.from(owner);

        //if (customVillager.getWorkstationLocation() == null)return;
        customVillager.removeWorkingStation();
    }
}
