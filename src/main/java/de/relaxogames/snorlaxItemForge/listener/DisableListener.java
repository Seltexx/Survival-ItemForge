package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.snorlaxItemForge.FileManager;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class DisableListener implements Listener {

    private FileManager fileManager = new FileManager();

    @EventHandler
    public void onCrystal(EntityExplodeEvent e){
        if (!fileManager.disabledEndCrystals())return;
        Entity exploder = e.getEntity();
        if (!(exploder instanceof EnderCrystal crystal))return;
        e.setCancelled(true);
        crystal.remove();
        exploder.getLocation().getWorld().spawnParticle(Particle.HEART, e.getLocation(), 20, 3, 3, 3);
    }

    @EventHandler
    public void onTNTExplode(TNTPrimeEvent e){
        if (!fileManager.disabledTNT())return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityDamageByEntityEvent e){
        Entity entity = e.getEntity();

        // TNT
        if (entity instanceof TNTPrimed && fileManager.disabledTNT()) {
            e.setCancelled(true);
            entity.remove();
            entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation(), 20, 3,3,3);
            return;
        }

        // TNT-Minecart
        if (entity instanceof ExplosiveMinecart && fileManager.disabledTNTMinecart()) {
            e.setCancelled(true);
            entity.remove();
            entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation(), 20, 3,3,3);
            return;
        }

        // Ender Crystal
        if (entity instanceof EnderCrystal && fileManager.disabledEndCrystals()) {
            e.setCancelled(true);
            entity.remove();
            entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation(), 20, 3,3,3);
        }
    }

    @EventHandler
    public void onTNTMinecart(EntityExplodeEvent e){
        if (!fileManager.disabledTNTMinecart())return;
        Entity exploder = e.getEntity();
        if (!(exploder instanceof ExplosiveMinecart tntMinecart))return;
        e.setCancelled(true);
        tntMinecart.remove();
        exploder.getLocation().getWorld().spawnParticle(Particle.HEART, e.getLocation(), 20, 3, 3, 3);
    }

}
