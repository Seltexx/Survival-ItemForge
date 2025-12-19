package de.relaxogames.snorlaxItemForge.listener;

import com.comphenix.protocol.PacketType;
import de.relaxogames.api.Lingo;
import de.relaxogames.languages.Locale;
import de.relaxogames.snorlaxItemForge.ItemForge;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Light;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LightBlockListener implements Listener {

    private final Map<UUID, BukkitRunnable> tasks = new HashMap<>();

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent e){
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItem(e.getNewSlot());

        // Prüfen ob Light-Block in der Hand
        if (item != null && item.getType() == Material.LIGHT && item.hasItemMeta()) {

            // Task starten, um Partikel anzuzeigen
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    showLightBlocks(player);
                }
            };
            task.runTaskTimer(ItemForge.getForge(), 0, 20); // alle 20 Ticks = 1 Sekunde
            tasks.put(player.getUniqueId(), task);
        } else {
            // Task stoppen, wenn kein Light-Item gehalten wird
            if (tasks.containsKey(player.getUniqueId())) {
                tasks.get(player.getUniqueId()).cancel();
                tasks.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        // Task stoppen, wenn Spieler geht
        UUID uuid = e.getPlayer().getUniqueId();
        if (tasks.containsKey(uuid)) {
            tasks.get(uuid).cancel();
            tasks.remove(uuid);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        if (tasks.containsKey(uuid)) {
            tasks.get(uuid).cancel();
            tasks.remove(uuid);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        if (tasks.containsKey(uuid)) {
            tasks.get(uuid).cancel();
            tasks.remove(uuid);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if (!e.getBlockPlaced().getType().equals(Material.LIGHT))return;
        if (e.getPlayer().getInventory().getItemInMainHand().getAmount() <= 1) {
            UUID uuid = e.getPlayer().getUniqueId();
            if (tasks.containsKey(uuid)) {
                tasks.get(uuid).cancel();
                tasks.remove(uuid);
            }
        }
    }

    @EventHandler
    public void onBreak(PlayerInteractEvent e){
        Player interacter = e.getPlayer();
        if (!interacter.getInventory().getItemInMainHand().getType().equals(Material.LIGHT) && !interacter.getInventory().getItemInOffHand().getType().equals(Material.LIGHT))return;
        if (!e.getAction().equals(Action.LEFT_CLICK_BLOCK))return;
        Block interact = e.getClickedBlock();
        if (!(interact.getBlockData() instanceof Light holdedLight))return;

        int lvl = holdedLight.getLevel();
        interact.breakNaturally();

        ItemStack lightItem = new ItemStack(Material.LIGHT, 1);
        BlockDataMeta meta = (BlockDataMeta) lightItem.getItemMeta();
        Light lightData = (Light) Bukkit.createBlockData(Material.LIGHT);
        lightData.setLevel(lvl);
        meta.setCustomModelData(100);
        meta.setBlockData(lightData);
        meta.customName(Component.text(
                Lingo.getLibrary().getMessage(Locale.GERMAN, "Light-Block-Name").replace("{LEVEL}", String.valueOf(lvl))
        ));
        lightItem.setItemMeta(meta);

        interact.getWorld().dropItem(interact.getLocation(), lightItem);
    }

    private void showLightBlocks(Player player){
        Location playerLoc = player.getLocation();
        int radius = 25;

        // Durch alle Blöcke im Würfel um den Spieler
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = playerLoc.clone().add(x, y, z);
                    Block block = loc.getBlock();

                    if (block.getType() == Material.LIGHT) {
                        // Optional: Level auslesen
                        BlockData data = block.getBlockData();
                        int level = 0;
                        if (data instanceof Light light) {
                            level = light.getLevel();
                        }

                        // Partikel an Block
                        player.spawnParticle(Particle.END_ROD, block.getLocation().add(0.5,0.5,0.5), 5, 0.3, 0.3, 0.3, 0);
                    }
                }
            }
        }
    }
}

