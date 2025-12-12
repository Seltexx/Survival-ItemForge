package de.relaxogames.snorlaxItemForge.util;

import com.destroystokyo.paper.ParticleBuilder;
import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CauldronManager {

    private FileManager fileManager = new FileManager();

    private final Block cauldron;
    private BlockData paperBlockData;
    private final Player toggler;
    private ItemStack itemStack;
    private ItemStack singleBottle;
    private final CustomBlockData cauldronData;
    private final Location cauldronLoc;

    private int level = 0;

    private final NamespacedKey hasTinctureKey = new NamespacedKey(ItemForge.getForge(), "contains_tincture");
    private final NamespacedKey tinctureLevelKey = new NamespacedKey(ItemForge.getForge(), "tincture_level");
    private final NamespacedKey bottleKey = new NamespacedKey(ItemForge.getForge(), "left_filling");

    public CauldronManager(Block cauldron, Player toggler, ItemStack itemStack) {
        this.cauldron = cauldron;
        this.toggler = toggler;
        this.itemStack = itemStack;
        this.cauldronData = new CustomBlockData(cauldron, ItemForge.getForge());
        this.paperBlockData = cauldron.getBlockData();
        this.cauldronLoc = cauldron.getLocation();
        this.singleBottle = itemStack.clone();
        this.singleBottle.setAmount(1);
    }

    public CauldronManager(Block cauldron, Player toggler) {
        this.cauldron = cauldron;
        this.toggler = toggler;
        this.cauldronData = new CustomBlockData(cauldron, ItemForge.getForge());
        this.paperBlockData = cauldron.getBlockData();
        this.cauldronLoc = cauldron.getLocation();
    }

    public int getLevel() {
        Integer stored = cauldronData.get(tinctureLevelKey, PersistentDataType.INTEGER);
        this.level = stored == null ? 0 : stored;
        return this.level;
    }

    public void setLevel(int lvl) {
        cauldronData.set(tinctureLevelKey, PersistentDataType.INTEGER, lvl);
        this.level = lvl;
        update(lvl);
    }

    public void update(int level){
        if (level <= 0) {
            cauldron.setType(Material.CAULDRON);
            paperBlockData = cauldron.getBlockData();
            return;
        }
        if (paperBlockData instanceof Levelled levelled) {
            levelled.setLevel(getLevel());
            cauldron.setBlockData(paperBlockData);
        }
    }

    public void update(){
        if (getLevel() <= 0) {
            cauldron.setType(Material.CAULDRON);
            paperBlockData = cauldron.getBlockData();
            return;
        }
        if (paperBlockData instanceof Levelled levelled) {
            levelled.setLevel(getLevel());
            cauldron.setBlockData(paperBlockData);
        }
    }

    public void updateMainHand(){
        ItemMeta itemMeta = toggler.getInventory().getItemInMainHand().getItemMeta();
        int leftFilling = itemMeta.getPersistentDataContainer().get(bottleKey, PersistentDataType.INTEGER) == null ? 0 : itemMeta.getPersistentDataContainer().get(bottleKey, PersistentDataType.INTEGER);
        itemMeta.lore(ItemBuilder.updateLore(new LingoPlayer(toggler.getUniqueId()).getLanguage(), leftFilling));
        toggler.getInventory().getItemInMainHand().setItemMeta(itemMeta);
    }

    public boolean containsTincture() {
        return cauldronData.get(hasTinctureKey, PersistentDataType.BOOLEAN) == null ? false : cauldronData.get(hasTinctureKey, PersistentDataType.BOOLEAN);
    }

    /**
     * Setzt das Cauldron-Level auf maximal möglich, basierend auf Bottle-Level
     */
    // === in deiner Klasse (ersetze die vorhandenen Methoden) ===

    public void addAllLevel() {

        // Cauldron darf nur befüllt werden, wenn er leer ist ODER Tinktur enthält
        if (!containsTincture() && cauldron.getType() != Material.CAULDRON) return;

        // Cauldron ist leer -> zu PowderSnowCauldron machen
        if (getLevel() == 0 && cauldron.getType() == Material.CAULDRON) {
            cauldron.setType(Material.POWDER_SNOW_CAULDRON);
            paperBlockData = cauldron.getBlockData();
        }

        // *** WICHTIG: Nur 1 Item wird verändert ***
        ItemMeta meta = singleBottle.getItemMeta();
        if (meta == null) return;

        if (!meta.hasCustomModelData()) return;
        if (meta.getCustomModelData() != 25) return;

        // Bottle Level laden
        Integer bottleLvl = meta.getPersistentDataContainer()
                .get(bottleKey, PersistentDataType.INTEGER);

        int bottleLevel = bottleLvl == null ? 0 : bottleLvl;

        if (bottleLevel <= 0) {
            removeOnePotionAndAddGlassBottle();
            return;
        }

        int maxLevel = fileManager.maxTincutureCauldronLevel();

        if (getLevel() >= maxLevel){
            update();
            updateMainHand();
            return;
        }

        // ▶ KORREKT: Wie viel in den Cauldron passt
        int currentLevel = getLevel();
        int spaceLeft = 3 - currentLevel;

        // ▶ KORREKT: nur so viel wie Flasche hat
        int fillAmount = Math.min(spaceLeft, bottleLevel);

        if (fillAmount <= 0) return;

        // Cauldron-Level setzen
        setLevel(currentLevel + fillAmount);

        // Markiere Cauldron als Tinktur-Cauldron
        cauldronData.set(hasTinctureKey, PersistentDataType.BOOLEAN, true);

        // Bottle-Level reduzieren
        int newBottleLevel = bottleLevel - fillAmount;
        meta.getPersistentDataContainer().set(bottleKey, PersistentDataType.INTEGER, newBottleLevel);

        // LORE AKTUALISIEREN (auf der neuen Meta)
        meta.lore(ItemBuilder.updateLore(new LingoPlayer(toggler.getUniqueId()).getLanguage(), newBottleLevel));

        // Meta in singleBottle setzen
        singleBottle.setItemMeta(meta);

        // Jetzt die Inventar-Änderung durchführen:
        // - wenn Flasche leer => entferne eine und gib Glasflasche
        // - ansonsten => ersetze genau 1 Flasche durch die aktualisierte singleBottle
        if (newBottleLevel <= 0) {
            removeOnePotionAndAddGlassBottle();
        } else {
            replaceOneBottleWithUpdated(meta);
        }

        //Sound abspielen
        cauldronLoc.getWorld().playSound(cauldronLoc, Sound.BLOCK_BREWING_STAND_BREW, 1000, 1.5F);

        // Partikel
        ParticleBuilder particleBuilder = Particle.WAX_OFF.builder();

        Location base = cauldronLoc.clone();

        // Schrittweite
        double step = 0.2;

        // Vier Seiten des Blocks markieren
        for (double i = 0; i <= 1; i += step) {

            // Vorderseite
            particleBuilder.location(base.clone().add(i, 1, 0)).receivers(32, true).spawn();

            // Rückseite
            particleBuilder.location(base.clone().add(i, 0, 1)).receivers(32, true).spawn();

            // Linke Seite
            particleBuilder.location(base.clone().add(0, 1, i)).receivers(32, true).spawn();

            // Rechte Seite
            particleBuilder.location(base.clone().add(1, 0, i)).receivers(32, true).spawn();
        }

        // Block visuell aktualisieren
        if (paperBlockData instanceof Levelled levelled) {
            levelled.setLevel(getLevel());
            cauldron.setBlockData(paperBlockData);
        }
    }

    public void depleteLevel(){
        if (level <= 0)return;
        if (level == 1){
            // Markierung Cauldron als Tinktur-Cauldron entfernen
            cauldronData.set(hasTinctureKey, PersistentDataType.BOOLEAN, false);
            setLevel(0);

            //Sound Abspielen
            cauldronLoc.getWorld().playSound(cauldronLoc, Sound.ENTITY_EVOKER_CAST_SPELL, 1000, 0.7F);

            // Partikel
            ParticleBuilder particleBuilder = Particle.WAX_OFF.builder();

            Location base = cauldronLoc.clone();

            // Schrittweite
            double step = 0.2;

            // Vier Seiten des Blocks markieren
            for (double i = 0; i <= 1; i += step) {

                // Vorderseite
                particleBuilder.location(base.clone().add(i, 1, 0)).receivers(32, true).spawn();

                // Rückseite
                particleBuilder.location(base.clone().add(i, 0, 1)).receivers(32, true).spawn();

                // Linke Seite
                particleBuilder.location(base.clone().add(0, 1, i)).receivers(32, true).spawn();

                // Rechte Seite
                particleBuilder.location(base.clone().add(1, 0, i)).receivers(32, true).spawn();
            }


            return;
        }
        setLevel(getLevel() - 1);

        //Sound Abspielen
        cauldronLoc.getWorld().playSound(cauldronLoc, Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1000, 1.5F);
        return;
    }

    /**
     * Verringert den Stack in der Hand um 1 und fügt die aktualisierte singleBottle (mit meta) dem Inventar hinzu.
     * Falls Stack == 1, ersetzt das Item direkt (setzt die Hand auf die aktualisierte singleBottle).
     */
    private void replaceOneBottleWithUpdated(ItemMeta updatedMeta) {
        ItemStack main = toggler.getInventory().getItemInMainHand();
        ItemStack off = toggler.getInventory().getItemInOffHand();

        // Hilfsfunktion prüft, ob ein Stack zur potion passt
        if (matchesTinctureStack(main)) {
            // Wenn mehrere im Stack, verringern und dann updated singleBottle hinzufügen
            if (main.getAmount() > 1) {
                main.setAmount(main.getAmount() - 1);
                ItemStack updated = singleBottle.clone();
                updated.setItemMeta(updatedMeta);
                updated.setAmount(1);
                toggler.getInventory().addItem(updated);
            } else {
                // Stack size == 1 -> direkt ersetzen (behalte Slot)
                ItemStack updated = singleBottle.clone();
                updated.setItemMeta(updatedMeta);
                toggler.getInventory().setItemInMainHand(updated);
            }
            return;
        }

        if (matchesTinctureStack(off)) {
            if (off.getAmount() > 1) {
                off.setAmount(off.getAmount() - 1);
                ItemStack updated = singleBottle.clone();
                updated.setItemMeta(updatedMeta);
                updated.setAmount(1);
                toggler.getInventory().addItem(updated);
            } else {
                ItemStack updated = singleBottle.clone();
                updated.setItemMeta(updatedMeta);
                toggler.getInventory().setItemInOffHand(updated);
            }
        }
    }

    /**
     * Entfernt eine Potion (Main oder Off) und gibt eine Glasflasche zurück.
     */
    private void removeOnePotionAndAddGlassBottle() {
        ItemStack main = toggler.getInventory().getItemInMainHand();
        ItemStack off = toggler.getInventory().getItemInOffHand();

        if (matchesTinctureStack(main)) {
            if (main.getAmount() > 1) {
                main.setAmount(main.getAmount() - 1);
            } else {
                toggler.getInventory().setItemInMainHand(null);
            }
            toggler.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE));
            return;
        }

        if (matchesTinctureStack(off)) {
            if (off.getAmount() > 1) {
                off.setAmount(off.getAmount() - 1);
            } else {
                toggler.getInventory().setItemInOffHand(null);
            }
            toggler.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE));
        }
    }

    /**
     * Prüft, ob das Item eine Tincture-Potion ist (CustomModelData 25 und PDC vorhanden).
     */
    private boolean matchesTinctureStack(ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;
        ItemMeta m = stack.getItemMeta();
        if (!m.hasCustomModelData()) return false;
        if (m.getCustomModelData() != 25) return false;
        // optional: prüfe, ob bottleKey existiert
        Integer lvl = m.getPersistentDataContainer().get(bottleKey, PersistentDataType.INTEGER);
        return lvl != null; // oder true, wenn du nur ModelData prüfen willst
    }

    public void delete(){
        cauldronData.clear();
    }

    public boolean hasData(){
        return !cauldronData.isEmpty();
    }
}
