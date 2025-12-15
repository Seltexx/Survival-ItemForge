package de.relaxogames.snorlaxItemForge.util.villager.villagertyps;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Beekeeper extends CustomVillager {
    public Beekeeper(Villager villager) {
        super(villager);
    }

    @Override
    public void acceptJob() {
        villager.setProfession(Villager.Profession.NITWIT);
        villager.setVillagerType(Villager.Type.SWAMP);
        villager.setVillagerLevel(1);
        villager.setRestocksToday(2);

        villager.getEquipment().setHelmet(getBeekeeperHead());
        villager.getEquipment().setHelmetDropChance(0.0F);
    }

    @Override
    public void workOnStation() {
        moveToOwnWorkingStation();
        getCurrentWorld().playSound(getWorkstationLocation(), Sound.BLOCK_BEEHIVE_SHEAR, 1000, 2);
        getCurrentWorld().playSound(getVillager().getLocation(), Sound.ENTITY_VILLAGER_YES, 1000, 1);
        Particle.ENTITY_EFFECT.builder()
                .location(getVillager().getLocation())
                .offset(0.5, 1, 0.5)
                .count(20)
                .data(Color.fromARGB(200, 255, 128, 0))
                .receivers(32, true)
                .spawn();
        replenishTrades();
    }

    @Override
    public void replenishTrades() {
        PotionEffect potionEffect = PotionEffectType.GLOWING.createEffect(5, 255);
        getVillager().addPotionEffect(potionEffect);
    }

    private ItemStack getBeekeeperHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.getProperties().add(new ProfileProperty(
                "textures",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjQ5NWM1ZTNiZTFiODAyZjQ1N2I2ZjljMmU1NGE5OWEwZmFlNWM4NTU2N2IzOTQ3NDg5MmVmOGY4YmE2N2RiZiJ9fX0="
        ));
        headMeta.setPlayerProfile(profile);
        head.setItemMeta(headMeta);
        return head;
    }
}
