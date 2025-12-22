package de.relaxogames.snorlaxItemForge.util;

import de.relaxogames.api.Lingo;
import de.relaxogames.languages.Locale;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        meta.displayName(title.decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(dataKey, PersistentDataType.INTEGER, data);
        is.setItemMeta(meta);
    }

    public void built(){
        is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();
        meta.displayName(title.decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        is.setItemMeta(meta);
    }

    public void addCustomModelData(int data){
        ItemMeta im = is.getItemMeta();
        im.setCustomModelData(data);
        is.setItemMeta(im);
    }

    public void setHeadTexture(String texture) {
        if (is.getType() != Material.PLAYER_HEAD) return;
        ItemMeta meta = is.getItemMeta();
        if (!(meta instanceof SkullMeta skullMeta)) return;
        // Use a stable UUID based on the texture string to ensure identical items for recipes
        UUID uuid = UUID.nameUUIDFromBytes(texture.getBytes());
        PlayerProfile profile = Bukkit.createProfile(uuid);
        profile.setProperty(new ProfileProperty("textures", texture));
        skullMeta.setPlayerProfile(profile);
        is.setItemMeta(skullMeta);
    }

    public void makeEdible(int nutrition, float saturation,
                           float consumeSeconds, ItemUseAnimation animation,
                           Key sound, boolean hasParticles, boolean canAlwaysEat,
                           List<ConsumeEffect> effects) {

        // Set food properties
        FoodProperties food = FoodProperties.food()
                .nutrition(nutrition)
                .saturation(saturation)
                .canAlwaysEat(canAlwaysEat)
                .build();
        is.setData(DataComponentTypes.FOOD, food);

        // Set consumable properties
        Consumable.Builder consumableBuilder = Consumable.consumable()
                .consumeSeconds(consumeSeconds)
                .animation(animation)
                .hasConsumeParticles(hasParticles);

        if (sound != null) {
            consumableBuilder.sound(sound);
        }

        if (effects != null && !effects.isEmpty()) {
            consumableBuilder.addEffects(effects);
        }

        is.setData(DataComponentTypes.CONSUMABLE, consumableBuilder.build());
    }
    public void makeEdible(int nutrition, float saturation) {
        makeEdible(nutrition, saturation, 1.6f, ItemUseAnimation.EAT,
                Key.key("entity.generic.eat"), true, false, null);
    }

    public void makeEdible(int nutrition, float saturation, boolean canAlwaysEat) {
        makeEdible(nutrition, saturation, 1.6f, ItemUseAnimation.EAT,
                Key.key("entity.generic.eat"), true, canAlwaysEat, null);
    }

    public void makeEdible(int nutrition, float saturation,
                           float consumeSeconds, ItemUseAnimation animation) {
        makeEdible(nutrition, saturation, consumeSeconds, animation,
                Key.key("entity.generic.eat"), true, false, null);
    }

    public void makeEdible(int nutrition, float saturation,
                           List<ConsumeEffect> effects) {
        makeEdible(nutrition, saturation, 1.6f, ItemUseAnimation.EAT,
                Key.key("entity.generic.eat"), true, false, effects);
    }

    public void makeDrinkable(int nutrition, float saturation) {
        makeEdible(nutrition, saturation, 1.6f, ItemUseAnimation.DRINK,
                Key.key("entity.generic.drink"), true, false, null);
    }

    public void makeDrinkable(int nutrition, float saturation,
                              List<ConsumeEffect> effects) {
        makeEdible(nutrition, saturation, 1.6f, ItemUseAnimation.DRINK,
                Key.key("entity.generic.drink"), true, false, effects);
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
                .replace("{5}", leftFilling >= 5 ? "§a" : "§7")
                .replace("{6}", leftFilling >= 6 ? "§a" : "§7"));
    }

    public static byte[] serializeItem(ItemStack item) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             BukkitObjectOutputStream oos = new BukkitObjectOutputStream(out)) {

            oos.writeObject(item);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack deserializeItem(byte[] data) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data);
             BukkitObjectInputStream ois = new BukkitObjectInputStream(in)) {

            return (ItemStack) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
