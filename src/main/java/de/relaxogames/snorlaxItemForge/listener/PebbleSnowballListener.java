package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

public class PebbleSnowballListener implements Listener {

    private final Random RANDOM = new Random();

    @EventHandler
    public void onThrow(EntityDamageByEntityEvent e) {

        if (e.getDamager().getType() != EntityType.SNOWBALL) return;

        if (RANDOM.nextInt(100) >= 40) return;

        Snowball snowball = (Snowball) e.getDamager();
        if (!(snowball.getShooter() instanceof Player player)) return;

        e.setDamage(1.0);

        Advancements.playout(player, Advancement.PEBBLED_SNOWBALL);

        if (e.getEntity() instanceof Player playerHit) {
            if (e.getFinalDamage() >= playerHit.getHealth()) {
                Advancements.playout(player, Advancement.PEBBLED_SNOWBALL_DEATH);
            }
        }
    }


}
