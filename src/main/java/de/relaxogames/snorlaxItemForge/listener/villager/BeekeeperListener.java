package de.relaxogames.snorlaxItemForge.listener.villager;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import de.relaxogames.snorlaxItemForge.listener.villager.events.PlayerEngageBeeOfBeekeeperEvent;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;

public class BeekeeperListener implements Listener {
    @EventHandler
    public void onBeeDamage(EntityDamageByEntityEvent e) {
        Entity killedBee = e.getEntity();
        if (!(killedBee instanceof Bee)) return;
        Entity damager = e.getDamager();
        if (!(damager instanceof Player damagerPlayer)) return;
        ArrayList<CustomVillager> angryVillager = new ArrayList<>();
        for (Entity entitiesNearby : damagerPlayer.getNearbyEntities(15, 10, 15)) {
            if (!(entitiesNearby instanceof Villager villagerNearby)) continue;
            CustomVillager villager = VillagerWrapper.load(villagerNearby);
            if (villager == null) continue;
            if (!villager.getProfession().equals(CustomVillager.Profession.BEEKEEPER)) return;
            angryVillager.add(villager);
        }
        if (angryVillager.isEmpty())return;
        PlayerEngageBeeOfBeekeeperEvent hitBeeEvent = new PlayerEngageBeeOfBeekeeperEvent(damagerPlayer, killedBee, angryVillager);
        Bukkit.getPluginManager().callEvent(hitBeeEvent);
    }

    @EventHandler
    public void onBeeDamage(PlayerEngageBeeOfBeekeeperEvent e){
        Player hitter = e.getDamager();
        Reputation brokenRep = new Reputation();
        brokenRep.setReputation(e.getHitBee().isDead() ? ReputationType.MAJOR_NEGATIVE : ReputationType.MINOR_NEGATIVE, 4);
        for (CustomVillager villagerPissedOff : e.getAngryVillager()){
            villagerPissedOff.getVillager().setReputation(hitter.getUniqueId(), brokenRep);
            Bukkit.broadcast(Component.text("KAPPUTTE REPUTATION!"));
        }
    }
}
