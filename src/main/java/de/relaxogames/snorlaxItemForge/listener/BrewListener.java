package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.api.Lingo;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.api.interfaces.LingoUser;
import de.relaxogames.languages.Locale;
import de.relaxogames.languages.ServerColors;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import de.relaxogames.snorlaxItemForge.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class BrewListener implements Listener {

    private FileManager fileManager = new FileManager();

    private final Map<Location, UUID> lastBrewer = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof BrewerInventory inv)) return;

        ItemStack item;

        // Wenn Shift-Click vom Player-Inventory in Braustand
        if (event.isShiftClick() && event.getClickedInventory() != null && !(event.getClickedInventory() instanceof BrewerInventory)) {
            item = event.getCurrentItem();
        } else {
            item = event.getCursor();
        }

        if (item == null || item.getType() == Material.AIR) return;
        if (!item.hasItemMeta()) return;
        if (!item.getItemMeta().hasCustomModelData()) return;
        if (item.getItemMeta().getCustomModelData() != 12) return;

        BrewingStand stand = (BrewingStand) inv.getHolder();
        if (stand == null) return;

        Player player = (Player) event.getWhoClicked();

        lastBrewer.putIfAbsent(
                stand.getBlock().getLocation(),
                player.getUniqueId()
        );
    }

    // ✅ DRAG SUPPORT
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

        lastBrewer.putIfAbsent(
                stand.getBlock().getLocation(),
                player.getUniqueId()
        );
    }

    // ✅ BREW EVENT (STABIL)
    @EventHandler
    public void onBrew(BrewEvent event) {
        BrewerInventory inv = event.getContents();
        BrewingStand stand = (BrewingStand) inv.getHolder();
        if (stand == null) return;

        Location loc = stand.getBlock().getLocation();
        if (!lastBrewer.containsKey(loc)) return;

        Player brewer = Bukkit.getPlayer(lastBrewer.get(loc));
        if (brewer == null) return;

        LingoUser lingoBrewer = new LingoPlayer(brewer.getUniqueId());

        ItemStack ingredient = inv.getIngredient();
        if (ingredient == null || !ingredient.hasItemMeta()) return;
        if (!ingredient.getItemMeta().hasCustomModelData()) return;
        if (ingredient.getItemMeta().getCustomModelData() != 12) return;

        stand.setFuelLevel(Math.max(stand.getFuelLevel() - 1, 0));

        event.setCancelled(true);

        Random random = new Random();
        boolean potionsBrewed = false;
        for (int i = 0; i < 3; i++) {
            if (random.nextBoolean()) {

                ItemStack result = new ItemStack(Material.POTION);
                PotionMeta meta = (PotionMeta) result.getItemMeta();
                meta.setCustomModelData(25);
                meta.setBasePotionType(PotionType.MUNDANE);
                meta.displayName(Component.text(
                        Lingo.getLibrary().getMessage(
                                lingoBrewer.getLanguage(),
                                "Item-Invisibility-Tincture"
                        )
                ).color(ServerColors.DodgerBlue3.color())
                        .decoration(TextDecoration.ITALIC, false));

                meta.getPersistentDataContainer().set(
                        new NamespacedKey(ItemForge.getForge(), "left_filling"),
                        PersistentDataType.INTEGER,
                        fileManager.maxTincutureUses()
                );
                result.setItemMeta(meta);
                meta.getPersistentDataContainer().set(
                        new NamespacedKey(ItemForge.getForge(), "brewer"),
                        PersistentDataType.STRING,
                        brewer.getUniqueId().toString()
                );

                Integer leftFilling = result.getPersistentDataContainer().get(
                        new NamespacedKey(ItemForge.getForge(), "left_filling"),
                        PersistentDataType.INTEGER
                );
                if (leftFilling == null) leftFilling = fileManager.maxTincutureUses();
                meta.lore(ItemBuilder.updateLore(lingoBrewer.getLanguage(), leftFilling));

                meta.addCustomEffect(
                        new PotionEffect(PotionEffectType.UNLUCK, 20 * 60 * 5, 5),
                        true
                );
                meta.addCustomEffect(
                        new PotionEffect(PotionEffectType.NAUSEA, 20 * 10, 5),
                        false
                );

                meta.setColor(Color.fromRGB(255,255,255));

                meta.setMaxStackSize(8);
                result.setItemMeta(meta);
                result.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

                inv.setItem(i, result);
                potionsBrewed = true;

            } else {
                ItemStack mundanePotion = new ItemStack(Material.POTION);
                PotionMeta meta = (PotionMeta) mundanePotion.getItemMeta();
                meta.setBasePotionType(PotionType.MUNDANE);
                mundanePotion.setItemMeta(meta);
                inv.setItem(i, mundanePotion);
            }
        }

        if (potionsBrewed) {
            Advancements.playout(brewer, Advancement.BREWED_TINCTURE);
            if (random.nextInt(100) < 5) {
                loc.getWorld().createExplosion(loc, 1.5F);
                Advancements.playout(brewer, Advancement.GLOBAL_ALCHEMIST_EXPLODED);
            }
        }

        // ✅ KORREKTE STACK-LOGIK
        int newAmount = ingredient.getAmount() - 1;

        if (newAmount <= 0) {
            inv.setIngredient(new ItemStack(Material.AIR));
            lastBrewer.remove(loc); // ✅ Nur jetzt löschen!
        } else {
            ingredient.setAmount(newAmount);
        }
    }
}
