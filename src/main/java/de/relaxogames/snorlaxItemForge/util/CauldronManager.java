package de.relaxogames.snorlaxItemForge.util;

import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
    private final CustomBlockData cauldronData;

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
        toggler.getInventory().getItemInMainHand().setItemMeta(ItemBuilder.updateLore(new LingoPlayer(toggler.getUniqueId()).getLanguage(), itemStack));
    }

    public boolean containsTincture() {
        return cauldronData.get(hasTinctureKey, PersistentDataType.BOOLEAN) == null ? false : cauldronData.get(hasTinctureKey, PersistentDataType.BOOLEAN);
    }

    /**
     * Setzt das Cauldron-Level auf maximal möglich, basierend auf Bottle-Level
     */
    public void addAllLevel() {

        // Cauldron darf nur befüllt werden, wenn er leer ist ODER Tinktur enthält
        if (!containsTincture() && cauldron.getType() != Material.CAULDRON) return;

        // Cauldron ist leer -> zu PowderSnowCauldron machen
        if (getLevel() == 0 && cauldron.getType() == Material.CAULDRON) {
            cauldron.setType(Material.POWDER_SNOW_CAULDRON);
            paperBlockData = cauldron.getBlockData();
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        if (!meta.hasCustomModelData()) return;
        if (meta.getCustomModelData() != 25) return;


        // Bottle Level laden
        Integer bottleLvl = meta.getPersistentDataContainer()
                .get(bottleKey, PersistentDataType.INTEGER);

        int bottleLevel = bottleLvl == null ? 0 : bottleLvl;

        if (bottleLevel <= 0) {
            toggler.getInventory().remove(itemStack);
            return;
        }

        int maxLevel = fileManager.maxTincutureCauldronLevel();

        if (getLevel() >= maxLevel) return;

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

        // Meta speichern
        itemStack.setItemMeta(meta);

        // Wenn Flasche leer -> entfernen
        if (newBottleLevel <= 0) {
            toggler.getInventory().remove(itemStack);
            toggler.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE));
        }

        // Block visuell aktualisieren
        if (paperBlockData instanceof Levelled levelled) {
            levelled.setLevel(getLevel());
            cauldron.setBlockData(paperBlockData);
        }
        update();
    }
}
