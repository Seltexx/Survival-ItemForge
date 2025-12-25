package de.relaxogames.snorlaxItemForge.listener;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class DisableListener implements Listener {

    @EventHandler
    public void onCrystalExplode(EntityExplodeEvent e) {
        if (!(e.getEntity() instanceof EnderCrystal crystal))
            return;

        if (e.getLocation().getWorld().getEnvironment() != World.Environment.THE_END) {
            e.setCancelled(true);
            crystal.remove();
            e.getLocation().getWorld().spawnParticle(Particle.HEART, e.getLocation(), 20, 3, 3, 3);
        } else {
            e.blockList().clear();
        }
    }

    @EventHandler
    public void onTNTMinecartExplode(EntityExplodeEvent e) {
        if (!(e.getEntity() instanceof ExplosiveMinecart))
            return;
        e.blockList().clear();
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (damager instanceof EnderCrystal || damager instanceof ExplosiveMinecart) {
            e.setCancelled(true);
        }
    }
}
