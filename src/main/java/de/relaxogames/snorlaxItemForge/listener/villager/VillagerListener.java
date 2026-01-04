package de.relaxogames.snorlaxItemForge.listener.villager;

import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.listener.villager.events.CustomVillagerWorkTickEvent;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.PlayerTradeEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillagerListener implements Listener {

    private final NamespacedKey WORKING_TABLE_KEY = new NamespacedKey(ItemForge.getForge(), "working_station");
    protected static final NamespacedKey PROFESSION_KEY = new NamespacedKey(ItemForge.getForge(),
            "villager_profession");

    private final Map<UUID, Long> searchCooldown = new HashMap<>();

    @EventHandler
    public void onSearch(EntityMoveEvent e) {
        Entity mover = e.getEntity();

        if (!(mover instanceof Villager villager))
            return;
        if (!villager.isAdult())
            return;
        if (villager.getProfession() != Villager.Profession.NONE)
            return;
        if (!canSearchNow(villager))
            return;
        CustomVillager customVillager = VillagerWrapper.from(villager);
        if (customVillager == null || customVillager.getWorkstationLocation() != null)
            return;
        customVillager.moveToNearestWorkingStation();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        Entity tableOwner = e.getEntity();
        if (!(tableOwner instanceof Villager villager))
            return;
        CustomVillager diedVillager = VillagerWrapper.load(villager);
        if (diedVillager == null)
            return;

        diedVillager.removeWorkingStation();
        VillagerWrapper.uncache(villager.getUniqueId());
    }

    @EventHandler
    public void onUnload(EntityRemoveFromWorldEvent e) {
        if (e.getEntity() instanceof Villager villager) {
            VillagerWrapper.uncache(villager.getUniqueId());
        }
    }

    @EventHandler
    public void onCustomWorkTick(CustomVillagerWorkTickEvent e) {
        World world = e.getWorld();

        for (CustomVillager cv : VillagerWrapper.getCachedVillagers()) {
            if (cv.getCurrentWorld().equals(world)) {
                try {
                    cv.work(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onVillagerJob(VillagerCareerChangeEvent e) {
        Villager villager = e.getEntity();
        if (villager == null)
            return;
        if (!villager.getPersistentDataContainer().has(WORKING_TABLE_KEY))
            return;
        if (!villager.getPersistentDataContainer().has(PROFESSION_KEY))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onClick(PlayerInteractEntityEvent e) {
        Player interacter = e.getPlayer();
        Entity villagedInteracte = e.getRightClicked();
        if (!(villagedInteracte instanceof Villager villager))
            return;
        CustomVillager customVillager = VillagerWrapper.load(villager);
        if (customVillager == null)
            return;
        interacter.openMerchant(customVillager.getMerchant(), true);
    }

    @EventHandler
    public void onTrade(PlayerTradeEvent e) {
        Villager villager = (Villager) e.getVillager();
        if (villager == null)
            return;

        PersistentDataContainer pdc = villager.getPersistentDataContainer();
        if (!pdc.has(WORKING_TABLE_KEY) || !pdc.has(PROFESSION_KEY))
            return;

        CustomVillager customVillager = VillagerWrapper.load(villager);
        if (customVillager == null) {
            e.setCancelled(true);
            return;
        }

        // Standard XP vom Trade aktivieren
        e.setRewardExp(true);
        e.getTrade().setExperienceReward(true);
        customVillager.addExperience(1, villager);
    }

    public boolean canSearchNow(Villager villager) {
        long now = System.currentTimeMillis();
        Long last = searchCooldown.get(villager.getUniqueId());
        if (last != null && now - last < 5000)
            return false;

        searchCooldown.put(villager.getUniqueId(), now);
        return true;
    }
}
