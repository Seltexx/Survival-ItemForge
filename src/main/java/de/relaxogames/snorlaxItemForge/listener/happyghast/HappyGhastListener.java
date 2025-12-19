package de.relaxogames.snorlaxItemForge.listener.happyghast;

import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import io.papermc.paper.event.entity.EntityEffectTickEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class HappyGhastListener implements Listener {

    @EventHandler
    public void onFeed(PlayerInteractEntityEvent e){
        Player interacter = e.getPlayer();
        ItemStack inHand = e.getPlayer().getInventory().getItemInMainHand();

        if (inHand == null || inHand.getType().equals(Material.AIR))return;
        if (inHand.getItemMeta() == null)return;
        if (!inHand.getItemMeta().hasCustomModelData())return;
        Entity clickedOn = e.getRightClicked();

        if (clickedOn.isDead())return;
        if (!(clickedOn instanceof HappyGhast ghast))return;
        HippyGhast happyGhast = new CustomGhast(ghast);

        switch (inHand.getItemMeta().getCustomModelData()){
            case 111:{
                happyGhast.applySpeed(0.06, 450);
                e.setCancelled(true);
                break;
            }

            case 222:{
                happyGhast.applySpeed(0.07, 400);
                e.setCancelled(true);
                break;
            }

            case 333:{
                happyGhast.applySpeed(0.09, 350);
                e.setCancelled(true);
                break;
            }

            case 444:{
                happyGhast.applySpeed(0.1, 300);
                e.setCancelled(true);
                break;
            }
        }
        inHand.setAmount(inHand.getAmount()-1);
        happyGhast.getEntity().getLocation().getWorld().playSound(
                happyGhast.getEntity().getLocation(), Sound.ITEM_HONEY_BOTTLE_DRINK, 1F, 0
        );
        Advancements.playout(interacter, Advancement.PILOT_BEE);
    }

    @EventHandler
    public void onRide(EntityMountEvent e){
        if (!(e.getEntity() instanceof Player rider))return;
        if (!(e.getMount() instanceof HappyGhast ghast))return;
        HippyGhast hippyGhast = new CustomGhast(ghast);

        hippyGhast.addRider(rider);
    }

    @EventHandler
    public void onOffRide(EntityDismountEvent e){
        if (!(e.getEntity() instanceof Player rider))return;
        if (!(e.getDismounted() instanceof HappyGhast ghast))return;
        HippyGhast hippyGhast = new CustomGhast(ghast);

        hippyGhast.removeRider(rider);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e){
        if (!(e.getEntity() instanceof HappyGhast ghast))return;
        HippyGhast hippyGhast = new CustomGhast(ghast);
        hippyGhast.remove();
    }

}
