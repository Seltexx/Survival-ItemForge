package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.snorlaxItemForge.FileManager;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    @EventHandler (priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityDamageByEntityEvent e){
        Entity entity = e.getEntity();

        // TNT
        if (entity instanceof TNTPrimed && fileManager.disabledTNT()) {
            e.setCancelled(true);
            entity.remove();
            e.setDamage(0);
            entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation(), 20, 3,3,3);
            return;
        }

//        // TNT-Minecart
//        if (entity instanceof ExplosiveMinecart && fileManager.disabledTNTMinecart()) {
//            e.setCancelled(true);
//            entity.remove();
//            e.setDamage(0);
//            entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation(), 20, 3,3,3);
//            return;
//        }

        // Ender Crystal
        if (entity instanceof EnderCrystal && fileManager.disabledEndCrystals()) {
            e.setCancelled(true);
            e.setDamage(0);
            entity.remove();
            if (!entity.getWorld().getName().contains("end"))entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation(), 20, 3,3,3);
            else {
                entity.getWorld().createExplosion(entity.getLocation(), 2);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTNTMinecart(EntityExplodeEvent e){
        if (!fileManager.disabledTNTMinecart())return;
        Entity exploder = e.getEntity();
        if (!(exploder instanceof ExplosiveMinecart tntMinecart))return;
        e.setCancelled(true);
        exploder.getLocation().getWorld().spawnParticle(Particle.HEART, e.getLocation(), 20, 3, 3, 3);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMinecartExplosionDamage(EntityDamageEvent e) {
        if (!fileManager.disabledTNTMinecart()) return;
        DamageSource source = e.getDamageSource();
        if (source != null) return;

        e.setDamage(0);
        e.setCancelled(true);
    }


}
