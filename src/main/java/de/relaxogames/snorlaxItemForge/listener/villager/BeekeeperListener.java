package de.relaxogames.snorlaxItemForge.listener.villager;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;

public class BeekeeperListener implements Listener {

    @EventHandler
    public void onSearch(EntityMoveEvent e){
        Entity mover = e.getEntity();

        if (!(mover instanceof Villager villager))return;
        if (!villager.getProfession().equals(Villager.Profession.NONE))return;
        //if (villager.getPersistentDataContainer().get())
    }

}
