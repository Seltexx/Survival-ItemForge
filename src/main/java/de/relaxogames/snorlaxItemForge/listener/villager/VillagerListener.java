package de.relaxogames.snorlaxItemForge.listener.villager;

import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VillagerListener implements Listener {

    private FileManager fileManager = new FileManager();

    @EventHandler
    public void onSearch(EntityMoveEvent e){
        Entity mover = e.getEntity();

        if (!(mover instanceof Villager villager))return;
        System.out.println("2.1");
        CustomVillager customVillager = new CustomVillager(villager);
        if (customVillager == null)return;
        System.out.println("2.2");
        if (customVillager.hasPath())return;
        System.out.println("2.3");
        if (!villager.getProfession().equals(Villager.Profession.NONE) && customVillager.getProfession() != null)return;
        System.out.println("2.4");
        customVillager.moveToNearestWorkingStation();
    }



}
