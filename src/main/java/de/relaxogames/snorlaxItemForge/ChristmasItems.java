package de.relaxogames.snorlaxItemForge;

import de.relaxogames.api.Lingo;
import de.relaxogames.languages.Locale;
import de.relaxogames.snorlaxItemForge.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import java.util.Collections;

public class ChristmasItems {

    public static final NamespacedKey CHRISTMAS_ITEM_KEY = new NamespacedKey(ItemForge.getForge(), "christmas_item_type");

    public static ItemStack getUnbakedApple() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Unbaked-Apple-Name")),
                Material.APPLE,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Unbaked-Apple-Lore"))),
                CHRISTMAS_ITEM_KEY, 101
        );
        builder.addCustomModelData(101);
        ItemStack item = builder.getItem();
        item.setAmount(1);
        return item;
    }

    public static ItemStack getBakedApple() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Baked-Apple-Name")),
                Material.APPLE,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Baked-Apple-Lore"))),
                CHRISTMAS_ITEM_KEY, 102
        );
        builder.addCustomModelData(102);
        ItemStack item = builder.getItem();
        item.setAmount(1);
        return item;
    }

    public static ItemStack getHotChocolate() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Hot-Chocolate-Name")),
                Material.HONEY_BOTTLE,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Hot-Chocolate-Lore"))),
                CHRISTMAS_ITEM_KEY, 103
        );
        builder.addCustomModelData(103);
        ItemStack item = builder.getItem();
        item.setAmount(1);
        return item;
    }

    public static ItemStack getMulledWine() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Mulled-Wine-Name")),
                Material.HONEY_BOTTLE,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Mulled-Wine-Lore"))),
                CHRISTMAS_ITEM_KEY, 104
        );
        builder.addCustomModelData(104);
        ItemStack item = builder.getItem();
        item.setAmount(1);
        return item;
    }

    public static void registerRecipes() {
        // Unbaked Apple Recipe
        ShapelessRecipe unbakedAppleRecipe = new ShapelessRecipe(new NamespacedKey(ItemForge.getForge(), "unbaked_apple_recipe"), getUnbakedApple());
        unbakedAppleRecipe.addIngredient(Material.APPLE);
        unbakedAppleRecipe.addIngredient(Material.SUGAR);
        unbakedAppleRecipe.addIngredient(Material.SWEET_BERRIES);
        Bukkit.addRecipe(unbakedAppleRecipe);

        // Baked Apple Recipe (Furnace)
        FurnaceRecipe bakedAppleFurnace = new FurnaceRecipe(new NamespacedKey(ItemForge.getForge(), "baked_apple_furnace"), getBakedApple(), new RecipeChoice.ExactChoice(getUnbakedApple()), 0.35f, 200);
        Bukkit.addRecipe(bakedAppleFurnace);

        // Baked Apple Recipe (Smoker)
        SmokingRecipe bakedAppleSmoking = new SmokingRecipe(new NamespacedKey(ItemForge.getForge(), "baked_apple_smoking"), getBakedApple(), new RecipeChoice.ExactChoice(getUnbakedApple()), 0.35f, 100);
        Bukkit.addRecipe(bakedAppleSmoking);

        // Hot Chocolate Recipe
        ShapelessRecipe hotChocolateRecipe = new ShapelessRecipe(new NamespacedKey(ItemForge.getForge(), "hot_chocolate_recipe"), getHotChocolate());
        hotChocolateRecipe.addIngredient(Material.MILK_BUCKET);
        hotChocolateRecipe.addIngredient(Material.COCOA_BEANS);
        hotChocolateRecipe.addIngredient(Material.SUGAR);
        Bukkit.addRecipe(hotChocolateRecipe);

        // Glühwein Recipe
        ShapelessRecipe mulledWineRecipe = new ShapelessRecipe(new NamespacedKey(ItemForge.getForge(), "mulled_wine_recipe"), getMulledWine());
        mulledWineRecipe.addIngredient(Material.GLASS_BOTTLE);
        mulledWineRecipe.addIngredient(Material.SWEET_BERRIES);
        mulledWineRecipe.addIngredient(Material.SUGAR);
        Bukkit.addRecipe(mulledWineRecipe);
    }
}
