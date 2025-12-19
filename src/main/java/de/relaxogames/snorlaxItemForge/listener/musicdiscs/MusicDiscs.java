package de.relaxogames.snorlaxItemForge.listener.musicdiscs;

import de.relaxogames.api.Lingo;
import de.relaxogames.languages.Locale;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public enum MusicDiscs {

    YMCA(
            "YMCA",
            "YMCA",
            Material.MUSIC_DISC_CHIRP,
            1978,
            Advancement.LISTENED_TO_YMCA,
            "Music-Disc-YMCA-Lore"
    ),

    RICK_ROLL(
            "RICKROLL",
            "RickRoll",
            Material.MUSIC_DISC_11,
            1960,
            Advancement.LISTENED_TO_RICKROLL,
            "Music-Disc-RICKROLL-Lore"
    ),

    GRIECHISCHER_WEIN(
            "GRIECHISCH",
            "griechischer-wein",
            Material.MUSIC_DISC_PIGSTEP,
            1970,
            Advancement.LISTENED_TO_GREEK_WINE,
            "Music-Disc-GRIECHISCH-Lore"
    ),

    GRAVITY_FALL_THEME(
            "GF",
            "gravity_fall_theme",
            Material.MUSIC_DISC_RELIC,
            2020,
            Advancement.LISTENED_TO_GRAVITY_FALLS_THEME,
            "Music-Disc-GF-Lore"
    ),

    BEETHOVENS_NO5(
            "BH-5",
            "Beethovens5",
            Material.MUSIC_DISC_LAVA_CHICKEN,
            1808,
            Advancement.LISTENED_TO_BEETHOVENS_NO5,
            "Music-Disc-BH-5-Lore"
    ),

    LET_IT_SNOW(
            "CHRISTMAS-25",
            "Let_it_snow",
            Material.MUSIC_DISC_TEARS,
            241225,
            Advancement.LISTENED_TO_LET_IT_SNOW,
            "Music-Disc-CHRISTMAS-25-Lore"
    );

    private final String key;
    private final String file;
    private final Material material;
    private final int customModelData;
    private final Advancement advancement;
    private final String loreKey;

    MusicDiscs(
            String key,
            String file,
            Material material,
            int customModelData,
            Advancement advancement,
            String loreKey
    ) {
        this.key = key;
        this.file = file;
        this.material = material;
        this.customModelData = customModelData;
        this.advancement = advancement;
        this.loreKey = loreKey;
    }

    public Advancement getAdvancement() {
        return advancement;
    }

    public String getFile() {
        return file;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    // =========================
    // FACTORY
    // =========================

    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setCustomModelData(customModelData);
        meta.customName(Component.text(
                Lingo.getLibrary().getMessage(Locale.GERMAN, "Music-Disc-" + key + "-Name")
        ));
        meta.lore(List.of(
                Component.text(
                        Lingo.getLibrary().getMessage(Locale.GERMAN, loreKey)
                )
        ));

        meta.addItemFlags(
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                ItemFlag.HIDE_STORED_ENCHANTS
        );

        item.setItemMeta(meta);
        return item;
    }

    // =========================
    // LOOKUP
    // =========================

    public static MusicDiscs fromCustomModelData(int cmd) {
        for (MusicDiscs disc : values()) {
            if (disc.customModelData == cmd) {
                return disc;
            }
        }
        return null;
    }

    public static ItemStack convertModelIdToItemStack(int cmd) {
        for (MusicDiscs disc : values()) {
            if (disc.customModelData == cmd) {
                return disc.createItem();
            }
        }
        return null;
    }
}
