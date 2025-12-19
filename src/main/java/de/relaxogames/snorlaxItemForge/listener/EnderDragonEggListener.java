package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.api.Lingo;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.api.interfaces.LingoUser;
import de.relaxogames.snorlaxItemForge.ItemForge;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
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
        Inventory clicked = event.getClickedInventory();
        if (clicked == null) return;

        Player player = (Player) event.getWhoClicked();

        ItemStack cursor = event.getCursor();       // Item vom Spieler
        ItemStack current = event.getCurrentItem(); // Item im Slot

        // === EGG WIRD IN CONTAINER GELEGT ===
        if (cursor != null
                && cursor.getType() == Material.DRAGON_EGG
                && clicked.getType() != InventoryType.PLAYER
                && clicked.getType() != InventoryType.CREATIVE) {

            event.setCancelled(true);

            Bukkit.getScheduler().runTask(ItemForge.getForge(), () -> {
                player.updateInventory();
            });
            return;
        }

        // === SHIFT-CLICK VOM PLAYER-INVENTAR ===
        if (current != null
                && current.getType() == Material.DRAGON_EGG
                && event.isShiftClick()
                && event.getInventory().getType() != InventoryType.PLAYER) {

            event.setCancelled(true);

            Bukkit.getScheduler().runTask(ItemForge.getForge(), player::updateInventory);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        ItemStack cursor = player.getItemOnCursor();

        if (cursor != null && cursor.getType() == Material.DRAGON_EGG) {
            player.setItemOnCursor(null);
            player.getInventory().addItem(cursor);

            Bukkit.getScheduler().runTask(ItemForge.getForge(), player::updateInventory);
        }
    }

    @EventHandler
    public void onBundleInteraction(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        if ((cursor != null && cursor.getType() == Material.DRAGON_EGG)
                || (current != null && current.getType() == Material.DRAGON_EGG)) {

            // Irgendein Bundle ist beteiligt?
            if ((cursor != null && cursor.getType() == Material.BUNDLE)
                    || (current != null && current.getType() == Material.BUNDLE)) {

                event.setCancelled(true);

                Player player = (Player) event.getWhoClicked();
                Bukkit.getScheduler().runTask(ItemForge.getForge(), player::updateInventory);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getOldCursor() != null &&
                event.getOldCursor().getType() == Material.DRAGON_EGG) {

            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            Bukkit.getScheduler().runTask(ItemForge.getForge(), player::updateInventory);
        }
    }

    /**
     * Prüft, ob das Dragon Egg eingebaut ist.
     * Eingebaut = das Ei ist nicht der höchste solide Block an der X/Z-Koordinate.
     */
    private boolean isDragonEggBuiltIn(Location eggLoc) {
        if (eggLoc == null) return false;

        World world = eggLoc.getWorld();
        int x = eggLoc.getBlockX();
        int z = eggLoc.getBlockZ();

        // von oberster Höhe nach unten prüfen
        for (int y = world.getMaxHeight(); y > eggLoc.getBlockY(); y--) {
            Block b = world.getBlockAt(x, y, z);
            if (!b.isEmpty() && b.getType().isSolid()) {
                return true; // Ei ist eingebaut
            }
        }

        return false; // Ei liegt oben / frei
    }

    @EventHandler
    public void onDragonEggTeleport(BlockFromToEvent event) {
        Block block = event.getBlock();

        if (block.getType() != Material.DRAGON_EGG) return;

        Location from = block.getLocation();
        Location to = event.getToBlock().getLocation();

        Bukkit.getLogger().info(
                "Dragon Egg teleportiert von: "
                        + from.getWorld().getName() + " "
                        + from.getBlockX() + " "
                        + from.getBlockY() + " "
                        + from.getBlockZ()
                        + " nach: "
                        + to.getWorld().getName() + " "
                        + to.getBlockX() + " "
                        + to.getBlockY() + " "
                        + to.getBlockZ()
        );
    }

    @EventHandler
    public void onDragonEggPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.DRAGON_EGG) return;

        Location loc = event.getBlockPlaced().getLocation();
        Player player = event.getPlayer();


        // Koordinaten weitergeben / loggen
        Bukkit.getLogger().info(
                "Dragon Egg platziert von " + player.getName() + " bei: "
                        + loc.getWorld().getName() + " "
                        + loc.getBlockX() + " "
                        + loc.getBlockY() + " "
                        + loc.getBlockZ()
        );
    }

    private void loadEnd() {
        if (end != null) return;
        for (World worlds : Bukkit.getWorlds()) {
            if (!worlds.getName().contains("the_end")) continue;
            end = worlds;
        }
    }
}
