package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.snorlaxItemForge.FileManager;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.TNTPrimeEvent;
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
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {

            DamageSource source = e.getDamageSource();

            // TNT-Minecart Explosion
            if (source.getCausingEntity() instanceof ExplosiveMinecart && fileManager.disabledTNTMinecart()) {
                e.setCancelled(true);
                e.setDamage(0);
                return;
            }

            // Endkristall Explosion
            if (source.getCausingEntity() instanceof EnderCrystal && fileManager.disabledEndCrystals()) {
                e.setCancelled(true);
                e.setDamage(0);
                return;
            }

            // Endkristall Explosion
            if (source.getCausingEntity() instanceof TNTPrimed && fileManager.disabledTNT()) {
                e.setCancelled(true);
                e.setDamage(0);
            }
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
