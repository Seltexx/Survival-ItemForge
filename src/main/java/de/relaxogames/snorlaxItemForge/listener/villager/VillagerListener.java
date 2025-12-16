package de.relaxogames.snorlaxItemForge.listener.villager;

import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.PlayerTradeEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class VillagerListener implements Listener {

    private FileManager fileManager = new FileManager();
    private static final NamespacedKey LAST_SEARCH =
            new NamespacedKey(ItemForge.getForge(), "last_search");
    private final NamespacedKey WORKING_TABLE_KEY = new NamespacedKey(ItemForge.getForge(), "working_station");
    protected static final NamespacedKey PROFESSION_KEY =
            new NamespacedKey(ItemForge.getForge(), "villager_profession");

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

        Bukkit.broadcast(Component.text("REMOVED: " + tableOwner.getUniqueId() + " BEI " + diedVillager.getWorkstationLocation().toString()));
        diedVillager.removeWorkingStation();
    }

    @EventHandler
    public void onReplenishTrades(VillagerReplenishTradeEvent e){
        for (Player onlinePlayers : Bukkit.getOnlinePlayers()){
            for (Entity nearbyEntity : onlinePlayers.getNearbyEntities(40, 40, 40)){
                if (!(nearbyEntity instanceof Villager villager))continue;
                CustomVillager customVillager = VillagerWrapper.load(villager);
                if (customVillager == null)continue;
                customVillager.work();
            }
        }
    }

    @EventHandler
    public void onVillagerJob(VillagerCareerChangeEvent e){
        Villager villager = e.getEntity();
        if (villager == null)return;
        if (!villager.getPersistentDataContainer().has(WORKING_TABLE_KEY))return;
        if (!villager.getPersistentDataContainer().has(PROFESSION_KEY))return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onClick(PlayerInteractEntityEvent e){
        Player interacter = e.getPlayer();
        Entity villagedInteracte = e.getRightClicked();
        if (!(villagedInteracte instanceof Villager villager))return;
        CustomVillager customVillager = VillagerWrapper.load(villager);
        if (customVillager == null)return;
        interacter.openMerchant(customVillager.getMerchant(), true);
    }

    @EventHandler
    public void onTrade(PlayerTradeEvent e){
        Villager villager = (Villager) e.getVillager();
        if (villager == null) return;

        PersistentDataContainer pdc = villager.getPersistentDataContainer();
        if (!pdc.has(WORKING_TABLE_KEY) || !pdc.has(PROFESSION_KEY)) return;

        CustomVillager customVillager = VillagerWrapper.load(villager);
        if (customVillager == null){
            e.setCancelled(true);
            return;
        }

        // Standard XP vom Trade aktivieren
        e.setRewardExp(true);
        e.getTrade().setExperienceReward(true);

        //// +1 XP zum Custom-Level-System
        //customVillager.addExperience(5);
        //addExperience(customVillager, villager.getVillagerExperience(), 5);
        // Optional: sofort GUI/Level synchronisieren
        customVillager.update();

        // Optional: Level-Up-Particles
        if (customVillager.getLevel() != null) {
            // z.B. HAPPY_VILLAGER Partikel
            villager.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,
                    villager.getLocation().add(0,1,0), 5, 0.3,0.5,0.3, 0.05);
        }
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

    private void addExperience(CustomVillager customVillager, int currentExp, int amount) {
        if (amount <= 0) return;
        if (customVillager.getLevel() == CustomVillager.Level.MASTER) return;

        // XP hinzufügen
        int newExp = currentExp + amount;
        CustomVillager.Level level = customVillager.getLevel();

        // Level-Up Loop
        while (level != CustomVillager.Level.MASTER && newExp >= level.getNeededExperience()) {
            newExp -= level.getNeededExperience();
            CustomVillager.Level nextLevel = CustomVillager.Level.nextLevel(level);
            if (nextLevel == null) {
                // MASTER erreicht, restliche XP verfallen
                newExp = 0;
                level = CustomVillager.Level.MASTER;
                break;
            }
            level = nextLevel;

            // Optional: Partikel/Sound beim Level-Up
            customVillager.getVillager().getWorld().spawnParticle(
                    Particle.HAPPY_VILLAGER,
                    customVillager.getVillager().getLocation().add(0,1,0),
                    10, 0.5, 0.5, 0.5, 0.1
            );
            customVillager.getVillager().getWorld().playSound(
                    customVillager.getVillager().getLocation(),
                    "entity.villager.yes",
                    1f, 1f
            );
        }

        // XP und Level speichern
        customVillager.getVillager().setVillagerExperience(newExp);
        customVillager.getVillager().setVillagerLevel(level.getNmsLevel());
        customVillager.getVillager().getPersistentDataContainer().set(
                new NamespacedKey(ItemForge.getForge(), "villager_profession_level"),
                PersistentDataType.INTEGER,
                level.getNmsLevel()
        );

        // CustomVillager-Level intern setzen
        customVillager.addExperience(amount); // sorgt dafür, dass dein CustomVillager-Objekt synchron bleibt
    }

}
