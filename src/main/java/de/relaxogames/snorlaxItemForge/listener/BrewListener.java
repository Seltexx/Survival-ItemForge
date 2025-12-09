package de.relaxogames.snorlaxItemForge.listener;

import com.destroystokyo.paper.ParticleBuilder;
import de.relaxogames.api.Lingo;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.api.interfaces.LingoUser;
import de.relaxogames.languages.ServerColors;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import de.relaxogames.snorlaxItemForge.advancement.Toast;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
        if (lastBrewer.get(stand.getBlock().getLocation()) == player.getUniqueId())return;
        lastBrewer.remove(stand.getBlock().getLocation());
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
        if (lastBrewer.get(stand.getBlock().getLocation()) == player.getUniqueId())return;
        lastBrewer.remove(stand.getBlock().getLocation());
        lastBrewer.put(stand.getBlock().getLocation(), player.getUniqueId());

    }

    // ✅ JETZT FUNKTIONIERT AUCH DAS BREW EVENT
    @EventHandler
    public void onBrew(BrewEvent event) {
        BrewerInventory inv = event.getContents();
        BrewingStand stand = (BrewingStand) inv.getHolder();
        if (stand == null) return;

        Location loc = stand.getBlock().getLocation();
        if (!lastBrewer.containsKey(loc)) {
            return;
        }

        Player brewer = Bukkit.getPlayer(lastBrewer.get(loc));
        if (brewer == null) return;
        LingoUser lingoBrewer = new LingoPlayer(brewer.getUniqueId());

        ItemStack ingredient = inv.getIngredient();
        if (ingredient == null || !ingredient.hasItemMeta()) return;
        if (!ingredient.getItemMeta().hasCustomModelData()) return;
        if (ingredient.getItemMeta().getCustomModelData() != 12) return;

        if (ingredient.getAmount() <= 1) {
            lastBrewer.remove(loc);
        }
        stand.setFuelLevel(Math.max(stand.getFuelLevel() - 2, 0));
        Random random = new Random();

        event.setCancelled(true);
        for (int i = 0; i < 3; i++) {
            if(random.nextInt(2) == 1) { //CHANCE 50%
                ItemStack bottle = inv.getItem(i);
                if (bottle == null || bottle.getType() != Material.POTION) continue;

                ItemStack result = new ItemStack(Material.POTION);
                PotionMeta meta = (PotionMeta) result.getItemMeta();

                meta.getPersistentDataContainer().set(new NamespacedKey(ItemForge.getForge(), "left_filling"), PersistentDataType.INTEGER, 5);
                meta.setBasePotionType(PotionType.MUNDANE);
                meta.displayName(Component.text(
                        Lingo.getLibrary().getMessage(lingoBrewer.getLanguage(), "Item-Invisibility-Tincture")
                ).color(TextColor.color(Color.FUCHSIA.asRGB())));

                meta.addCustomEffect(
                        new PotionEffect(PotionEffectType.GLOWING, 20 * 60, 3),
                        true
                );

                meta.setColor(Color.fromRGB(
                        ServerColors.Red3.getR(),
                        ServerColors.Red3.getG(),
                        ServerColors.Red3.getB()
                ));

                meta.setMaxStackSize(8);
                result.setItemMeta(meta);

                inv.setItem(i, result);
            }else { //CHANCE 50%
                ItemStack mundanePotion = new ItemStack(Material.POTION);
                PotionMeta meta = (PotionMeta) mundanePotion.getItemMeta();

                meta.setBasePotionType(PotionType.MUNDANE);
                mundanePotion.setItemMeta(meta);
                inv.setItem(i, mundanePotion);
            }
        }

        Advancements.playout(brewer, Advancement.GLOBAL_ALCHEMIST);
        if ((ingredient.getAmount() >= 1)) {
            inv.getIngredient().setAmount(ingredient.getAmount() - 1);
        } else {
            inv.setIngredient(new ItemStack(Material.AIR));
        }
    }
}
