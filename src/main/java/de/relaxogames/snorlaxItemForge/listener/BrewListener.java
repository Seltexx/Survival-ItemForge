package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.api.Lingo;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.api.interfaces.LingoUser;
import de.relaxogames.languages.ServerColors;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Toast;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BrewListener implements Listener {

    private final Map<Location, UUID> lastBrewer = new HashMap<>();

    // ✅ NORMALER KLICK (HÄUFIGSTER FALL)
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof BrewerInventory inv)) return;
        if (event.getSlot() != 3) return; // Ingredient Slot

        ItemStack item = event.getCursor(); // WICHTIG: Cursor beim Ablegen!
        if (item == null || item.getType() == Material.AIR) return;
        if (!item.hasItemMeta()) return;
        if (!item.getItemMeta().hasCustomModelData()) return;
        if (item.getItemMeta().getCustomModelData() != 12) return;

        BrewingStand stand = (BrewingStand) inv.getHolder();
        if (stand == null) return;

        Player player = (Player) event.getWhoClicked();
        lastBrewer.put(stand.getBlock().getLocation(), player.getUniqueId());

        Bukkit.broadcast(Component.text("CLICK → Brewer gespeichert"));
    }

    // ✅ DRAG ALS FALLBACK
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getInventory() instanceof BrewerInventory inv)) return;
        if (!event.getRawSlots().contains(3)) return;

        ItemStack item = event.getOldCursor();
        if (item == null || item.getType() == Material.AIR) return;
        if (!item.hasItemMeta()) return;
        if (!item.getItemMeta().hasCustomModelData()) return;
        if (item.getItemMeta().getCustomModelData() != 12) return;

        BrewingStand stand = (BrewingStand) inv.getHolder();
        if (stand == null) return;

        Player player = (Player) event.getWhoClicked();
        lastBrewer.put(stand.getBlock().getLocation(), player.getUniqueId());

        Bukkit.broadcast(Component.text("DRAG → Brewer gespeichert"));
    }

    // ✅ JETZT FUNKTIONIERT AUCH DAS BREW EVENT
    @EventHandler
    public void onBrew(BrewEvent event) {
        BrewerInventory inv = event.getContents();
        BrewingStand stand = (BrewingStand) inv.getHolder();
        if (stand == null) return;

        Location loc = stand.getBlock().getLocation();
        if (!lastBrewer.containsKey(loc)) {
            Bukkit.broadcast(Component.text("❌ KEIN Brewer gespeichert"));
            return;
        }

        Player brewer = Bukkit.getPlayer(lastBrewer.get(loc));
        if (brewer == null) return;

        ItemStack ingredient = inv.getIngredient();
        if (ingredient == null || !ingredient.hasItemMeta()) return;
        if (!ingredient.getItemMeta().hasCustomModelData()) return;
        if (ingredient.getItemMeta().getCustomModelData() != 12) return;

        // ✅ Custom Brewing – Vanilla abbrechen
        event.setCancelled(true);

        Bukkit.broadcast(Component.text("✅ CUSTOM BREW AKTIV"));

        // 🔥 HIER kommt DEIN Custom Potion Code rein

        lastBrewer.remove(loc);
        inv.setIngredient(new ItemStack(Material.AIR));
        stand.setFuelLevel(Math.max(stand.getFuelLevel() - 1, 0));
    }
}
