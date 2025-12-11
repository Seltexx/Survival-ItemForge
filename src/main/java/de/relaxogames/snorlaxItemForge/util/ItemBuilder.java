package de.relaxogames.snorlaxItemForge.util;

import de.relaxogames.api.Lingo;
import de.relaxogames.languages.Locale;
import de.relaxogames.snorlaxItemForge.ItemForge;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    ItemStack is;

    Material material;
    Component title;
    List<Component> lore;
    NamespacedKey dataKey;
    int data;

    public ItemBuilder(Component title, Material material, List<Component> lore, NamespacedKey dataKey, int data) {
        this.title = title;
        this.material = material;
        this.lore = lore;
        this.dataKey = dataKey;
        this.data = data;
        build();
    }

    public ItemBuilder(Component title, Material material, List<Component> lore) {
        this.title = title;
        this.material = material;
        this.lore = lore;
        built();
    }

    public void build(){
        is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();
        meta.itemName(title);
        meta.lore(lore);
        meta.getPersistentDataContainer().set(dataKey, PersistentDataType.INTEGER, data);
        is.setItemMeta(meta);
    }

    public void built(){
        is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();
        meta.itemName(title);
        meta.lore(lore);
        is.setItemMeta(meta);
    }

    public void addCustomModelData(int data){
        ItemMeta im = is.getItemMeta();
        im.setCustomModelData(data);
        is.setItemMeta(im);
    }

    public ItemStack getItem() {
        return is;
    }

    public static List<Component> updateLore(Locale locale, int filling){
        List<Component> lore = new ArrayList<>();
        for (int d = 1; d <= 6; d++) {
            String key = "Item-Tincture-Lore-" + d;
            String message = Lingo.getLibrary().getMessage(locale, key);
            lore.add(Component.text(replaceTinctureLore(locale, message, filling)));
        }

        return lore;
    }

    private static String replaceTinctureLore(Locale locale, String message, int leftFilling) {
        return message.replace("{FILLING}", Lingo.getLibrary().getMessage(locale, "Item-Tincture-Filling")
                .replace("{1}", leftFilling >= 1 ? "§a" : "§7")
                .replace("{2}", leftFilling >= 2 ? "§a" : "§7")
                .replace("{3}", leftFilling >= 3 ? "§a" : "§7")
                .replace("{4}", leftFilling >= 4 ? "§a" : "§7")
                .replace("{5}", leftFilling >= 5 ? "§a" : "§7"));
    }
}
