package de.relaxogames.snorlaxItemForge.listener.villager;

import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class VillagerListener implements Listener {

    private FileManager fileManager = new FileManager();
    private static final NamespacedKey LAST_SEARCH =
            new NamespacedKey(ItemForge.getForge(), "last_search");
    private final NamespacedKey WORKING_TABLE_KEY = new NamespacedKey(ItemForge.getForge(), "working_station");

    @EventHandler
    public void onSearch(EntityMoveEvent e){
        Entity mover = e.getEntity();

        if (!(mover instanceof Villager villager))return;
        if (!villager.isAdult())return;
        if (!canSearchNow(villager))return;
        CustomVillager customVillager = VillagerWrapper.from(villager);
        if (customVillager == null)return;
        if (customVillager.getWorkstationLocation() != null)return;
        if (customVillager.getProfession() != null && villager.getProfession().equals(Villager.Profession.NONE))return;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e){
        Entity tableOwner = e.getEntity();
        if (!(tableOwner instanceof Villager villager))return;
        CustomVillager diedVillager = VillagerWrapper.load(villager);
        if (diedVillager == null)return;
        CustomBlockData workTable = new CustomBlockData(diedVillager.getWorkingStation(), ItemForge.getForge());
        if (!workTable.has(WORKING_TABLE_KEY))return;
        if (!UUID.fromString(workTable.get(WORKING_TABLE_KEY, PersistentDataType.STRING)).equals(tableOwner.getUniqueId()))return;

        Bukkit.broadcast(Component.text("REMOVED: " + tableOwner.getUniqueId() + " BEI " + diedVillager.getWorkstationLocation().toString()));
        diedVillager.removeWorkingstation();
    }

    public boolean canSearchNow(Villager villager) {
        long now = System.currentTimeMillis();
        Long last = villager.getPersistentDataContainer().get(LAST_SEARCH, PersistentDataType.LONG);
        if (last == null){
            villager.getPersistentDataContainer().set(LAST_SEARCH, PersistentDataType.LONG, now);
            return true;
        }
        if (now - last < 5000) return false;

        villager.getPersistentDataContainer().set(LAST_SEARCH, PersistentDataType.LONG, now);
        return true;
    }

}
