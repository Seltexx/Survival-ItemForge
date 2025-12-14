package de.relaxogames.snorlaxItemForge.listener.villager;

import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

public class VillagerListener implements Listener {

    private FileManager fileManager = new FileManager();
    private static final NamespacedKey LAST_SEARCH =
            new NamespacedKey(ItemForge.getForge(), "last_search");

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
