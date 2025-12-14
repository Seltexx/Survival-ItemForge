package de.relaxogames.snorlaxItemForge.util.villager.villagertyps;

import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Beekeeper extends CustomVillager {
    public Beekeeper(Villager villager) {
        super(villager);
    }

    @Override
    public void work() {
        moveToOwnWorkingStation();
        getCurrentWorld().playSound(getWorkstationLocation(), Sound.BLOCK_BEEHIVE_SHEAR, 1000, 2);
        getCurrentWorld().playSound(getVillager().getLocation(), Sound.ENTITY_VILLAGER_YES, 1000, 1);
        replenishTrades();
    }

    @Override
    public void replenishTrades() {
        PotionEffect potionEffect = PotionEffectType.GLOWING.createEffect(5, 255);
        getVillager().addPotionEffect(potionEffect);
    }
}
