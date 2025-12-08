package de.relaxogames.snorlaxItemForge.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
}
