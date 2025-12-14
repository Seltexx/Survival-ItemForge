package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.api.Lingo;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.api.interfaces.LingoUser;
import de.relaxogames.snorlaxItemForge.ItemForge;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class EnderDragonEggListener implements Listener {

    World end;

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player quitter = e.getPlayer();
        if (!quitter.getInventory().contains(Material.DRAGON_EGG))return;
        loadEnd();
        if (end == null){
            ItemForge.getForge().getLogger().log(Level.SEVERE, "END DIMENSION KONNTE NICHT GELADEN WERDEN! DRAGONEGG WURDE NICHT ENTFERNT! Letzter Besitzer: " + quitter.getName());
            return;
        }
        quitter.getInventory().remove(Material.DRAGON_EGG);
        Location spawnLoc = end.getHighestBlockAt(0, 0).getLocation().add(0, 1, 0);
        if (!spawnLoc.isWorldLoaded())end.loadChunk(spawnLoc.getChunk());
        spawnLoc.getBlock().setType(Material.DRAGON_EGG);
        for (Player online : Bukkit.getOnlinePlayers()){
            LingoUser lo = new LingoPlayer(online.getUniqueId());
            online.sendMessage(Component.text(
                    Lingo.getLibrary().getMessage(lo.getLanguage(), "Dragonegg-Player-Left")
            ));
            online.playSound(online.getLocation(), Sound.BLOCK_BELL_USE, 1000, 0);
            online.playSound(online.getLocation(), Sound.BLOCK_BELL_RESONATE, 1000, 0);
        }
    }

    @EventHandler
    public void onHopper(InventoryPickupItemEvent e){
        if (!e.getInventory().getType().equals(InventoryType.HOPPER))return;
        if (!e.getItem().getItemStack().getType().equals(Material.DRAGON_EGG))return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        if (item.getType() != Material.DRAGON_EGG) return;

        if (event.getInventory() != null
                && event.getInventory().getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    private void loadEnd(){
        if (end != null)return;
        for (World worlds : Bukkit.getWorlds()){
            if (!worlds.getName().contains("the_end"))continue;
            end = worlds;
        }
    }
}
