package de.relaxogames.snorlaxItemForge.listener.villager;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import de.relaxogames.snorlaxItemForge.listener.villager.events.CustomVillagerWorkTickEvent;
import de.relaxogames.snorlaxItemForge.listener.villager.events.PlayerEngageBeeOfBeekeeperEvent;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
import io.papermc.paper.event.player.PlayerTradeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class BeekeeperListener implements Listener {

    @EventHandler
    public void onBeeDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Bee bee)) return;
        if (!(e.getDamager() instanceof Player player)) return;

        if (bee.getHealth() - e.getFinalDamage() <= 0) return;

        List<CustomVillager> angryVillagers = new ArrayList<>();

        for (Entity nearby : player.getNearbyEntities(15, 10, 15)) {
            if (!(nearby instanceof Villager villagerEntity)) continue;

            CustomVillager villager = VillagerWrapper.load(villagerEntity);
            if (villager == null) continue;
            if (villager.getProfession() != CustomVillager.Profession.BEEKEEPER) continue;

            angryVillagers.add(villager);
        }

        if (angryVillagers.isEmpty()) return;

        Bukkit.getPluginManager().callEvent(
                new PlayerEngageBeeOfBeekeeperEvent(player, bee, angryVillagers)
        );
    }

    @EventHandler
    public void onBeeHit(PlayerEngageBeeOfBeekeeperEvent e) {
        Player hitter = e.getDamager();

        Reputation rep = new Reputation();
        rep.setReputation(ReputationType.MINOR_NEGATIVE, 4);

        for (CustomVillager villager : e.getAngryVillager()) {
            villager.getVillager().setReputation(hitter.getUniqueId(), rep);
            increasePrices(villager.getVillager(), 0.2f);
        }
    }

    @EventHandler
    public void onBeeDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Bee bee)) return;
        if (!(bee.getKiller() instanceof Player player)) return;

        List<CustomVillager> angryVillagers = new ArrayList<>();

        for (Entity nearby : player.getNearbyEntities(15, 10, 15)) {
            if (!(nearby instanceof Villager villagerEntity)) continue;

            CustomVillager villager = VillagerWrapper.load(villagerEntity);
            if (villager == null) continue;
            if (villager.getProfession() != CustomVillager.Profession.BEEKEEPER) continue;

            angryVillagers.add(villager);
            Bukkit.broadcast(
                    Component.text("🐝 Die Imker sind wütend!")
                            .color(NamedTextColor.RED)
            );
        }

        if (angryVillagers.isEmpty()) return;

        Reputation rep = new Reputation();
        rep.setReputation(ReputationType.MAJOR_NEGATIVE, 4);

        for (CustomVillager villager : angryVillagers) {
            villager.getVillager().setReputation(player.getUniqueId(), rep);
            increasePrices(villager.getVillager(), 0.4f);
        }

        Bukkit.broadcast(
                Component.text("🐝 Die Imker sind wütend!")
                        .color(NamedTextColor.RED)
        );
    }

    @EventHandler
    public void onTrade(PlayerTradeEvent e){
        Player trader = e.getPlayer();
        Villager villager = (Villager) e.getVillager();

        if (villager == null || villager.isDead())return;
        CustomVillager customVillager = VillagerWrapper.load(villager);
        if (customVillager == null)return;
        if (customVillager.getProfession() == null)return;
        if (!customVillager.getProfession().equals(CustomVillager.Profession.BEEKEEPER))return;
        Advancements.playout(trader, Advancement.BEEKEEPER_FIRSTSTRADE);
    }

    private void increasePrices(Villager villager, float multiplier) {
        List<MerchantRecipe> recipes = new ArrayList<>(villager.getRecipes());

        for (MerchantRecipe recipe : recipes) {
            recipe.setDemand(recipe.getDemand() + 2);
            recipe.setPriceMultiplier(multiplier);
        }

        villager.setRecipes(recipes);
    }

}
