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

    private static final String UNBAKED_GINGERBREAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjk3NTRjNzg0ODQ1MzU1ODNjZGEyNjMxMjY3OTM0MDUyMDlkYjMxYTMwZGU3NTUzZGQ5NzIwYWFjNTdjNTUyZCJ9fX0=";
    private static final String GINGERBREAD_MAN_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2RiNTQ0MjlkZWM1YjNmN2VmNDBlOGJjM2ZmMTk5NWNlZjY5NmEzNTAyOGY3ODJmNjM5MWMyYjU1MTFiYmUifX19";
    private static final String UNBAKED_PUDDING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVlZjIyNWU2ZTk2NzljMzFjMzU1NjYyNTgyNTZjNzY3Y2U0N2I5NDU4NTZiM2RiYTgxNDkxNjU2ZmUyZTVjMyJ9fX0=";
    private static final String BAKED_PUDDING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE5NmRhNDNhOGU1OWY2NWY4ZmIwY2ZkY2IyOTU4ZDE5ODA2NmQ1MDE1ZGE5OTE4NjA4YmY1YjU2ZDc5ZmNlOCJ9fX0=";
    private static final String CANDY_CANE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjdjNzE0MGRhZDkwZTMwMWFhNThhNDY0Y2E4NDFjOTc4NWRjZTJlMGIzY2QyMjM3OGRkM2JiMGEzNWU3NDczYiJ9fX0=";
    private static final String HOT_CHOCOLATE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE3ZWJhYWIwZTA0ODU4MjIwYTYyZWMxNDAzMmY5NDdmY2JhM2MzMWYzZGQ3YTY0MWVkYTZjMzcyY2U5NmNjNiJ9fX0=";
    private static final String MULLED_WINE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmJkN2JkNjhlMDZiYTM3ZDIxODg1MTU4NDFmNzhjYWExNmU5ZjllMDU5NmFjYWUwODNkYTEzNGI1YjYwNjRmYyJ9fX0=";

    public static ItemStack getUnbakedApple() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Unbaked-Apple-Name")),
                Material.APPLE,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Unbaked-Apple-Lore"))),
                CHRISTMAS_ITEM_KEY, 101
        );
        builder.addCustomModelData(101);
        builder.makeEdible(1, 0.1f);
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
        builder.makeEdible(4, 0.3f);
        ItemStack item = builder.getItem();
        item.setAmount(1);
        return item;
    }

    public static ItemStack getHotChocolate() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Hot-Chocolate-Name")),
                Material.PLAYER_HEAD,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Hot-Chocolate-Lore"))),
                CHRISTMAS_ITEM_KEY, 103
        );
        builder.addCustomModelData(103);
        builder.setHeadTexture(HOT_CHOCOLATE_TEXTURE);
        builder.makeEdible(2, 0.2f);
        return builder.getItem();
    }

    public static ItemStack getMulledWine() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Mulled-Wine-Name")),
                Material.PLAYER_HEAD,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Mulled-Wine-Lore"))),
                CHRISTMAS_ITEM_KEY, 104
        );
        builder.addCustomModelData(104);
        builder.setHeadTexture(MULLED_WINE_TEXTURE);
        builder.makeEdible(2, 0.2f);
        return builder.getItem();
    }

    public static ItemStack getGingerbreadMan() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Gingerbread-Man-Name")),
                Material.PLAYER_HEAD,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Gingerbread-Man-Lore"))),
                CHRISTMAS_ITEM_KEY, 105
        );
        builder.addCustomModelData(105);
        builder.setHeadTexture(GINGERBREAD_MAN_TEXTURE);
        builder.makeEdible(2, 0.4f);
        return builder.getItem();
    }

    public static ItemStack getCandyCane() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Candy-Cane-Name")),
                Material.PLAYER_HEAD,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Candy-Cane-Lore"))),
                CHRISTMAS_ITEM_KEY, 106
        );
        builder.addCustomModelData(106);
        builder.setHeadTexture(CANDY_CANE_TEXTURE);
        builder.makeEdible(2, 0.1f);
        return builder.getItem();
    }

    public static ItemStack getChristmasPudding() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Christmas-Pudding-Name")),
                Material.PLAYER_HEAD,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Christmas-Pudding-Lore"))),
                CHRISTMAS_ITEM_KEY, 107
        );
        builder.addCustomModelData(107);
        builder.setHeadTexture(BAKED_PUDDING_TEXTURE);
        builder.makeEdible(8, 0.3f);
        return builder.getItem();
    }

    public static ItemStack getRoastedAlmonds() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Roasted-Almonds-Name")),
                Material.SWEET_BERRIES,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Roasted-Almonds-Lore"))),
                CHRISTMAS_ITEM_KEY, 108
        );
        builder.addCustomModelData(108);
        builder.makeEdible(4, 0.4f);
        return builder.getItem();
    }

    public static ItemStack getUnbakedGingerbread() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Unbaked-Gingerbread-Name")),
                Material.PLAYER_HEAD,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Unbaked-Gingerbread-Lore"))),
                CHRISTMAS_ITEM_KEY, 109
        );
        builder.addCustomModelData(109);
        builder.setHeadTexture(UNBAKED_GINGERBREAD_TEXTURE);
        builder.makeEdible(1, 0.1f);
        return builder.getItem();
    }

    public static ItemStack getSweetenedAlmonds() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Sweetened-Almonds-Name")),
                Material.SWEET_BERRIES,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Sweetened-Almonds-Lore"))),
                CHRISTMAS_ITEM_KEY, 110
        );
        builder.addCustomModelData(110);
        builder.makeEdible(1, 0.1f);
        return builder.getItem();
    }

    public static ItemStack getUnmixedPudding() {
        ItemBuilder builder = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Unmixed-Pudding-Name")),
                Material.PLAYER_HEAD,
                Collections.singletonList(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Unmixed-Pudding-Lore"))),
                CHRISTMAS_ITEM_KEY, 113
        );
        builder.addCustomModelData(113);
        builder.setHeadTexture(UNBAKED_PUDDING_TEXTURE);
        builder.makeEdible(2, 0.1f);
        return builder.getItem();
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

        // Unbaked Gingerbread Recipe
        ShapelessRecipe unbakedGingerbreadRecipe = new ShapelessRecipe(new NamespacedKey(ItemForge.getForge(), "unbaked_gingerbread_recipe"), getUnbakedGingerbread());
        unbakedGingerbreadRecipe.addIngredient(Material.WHEAT);
        unbakedGingerbreadRecipe.addIngredient(Material.SUGAR);
        unbakedGingerbreadRecipe.addIngredient(Material.COCOA_BEANS);
        unbakedGingerbreadRecipe.addIngredient(Material.EGG);
        Bukkit.addRecipe(unbakedGingerbreadRecipe);

        // Gingerbread Man Recipe (Smoker)
        SmokingRecipe gingerbreadSmoking = new SmokingRecipe(new NamespacedKey(ItemForge.getForge(), "gingerbread_smoking"), getGingerbreadMan(), new RecipeChoice.ExactChoice(getUnbakedGingerbread()), 0.35f, 100);
        Bukkit.addRecipe(gingerbreadSmoking);

        // Candy Cane Recipe
        ShapelessRecipe candyCaneRecipe = new ShapelessRecipe(new NamespacedKey(ItemForge.getForge(), "candy_cane_recipe"), getCandyCane());
        candyCaneRecipe.addIngredient(Material.SUGAR);
        candyCaneRecipe.addIngredient(Material.SUGAR);
        candyCaneRecipe.addIngredient(Material.RED_DYE);
        Bukkit.addRecipe(candyCaneRecipe);

        // Christmas Pudding Recipe
        ShapelessRecipe christmasPuddingRecipe = new ShapelessRecipe(new NamespacedKey(ItemForge.getForge(), "christmas_pudding_recipe"), getUnmixedPudding());
        christmasPuddingRecipe.addIngredient(Material.WHEAT);
        christmasPuddingRecipe.addIngredient(Material.EGG);
        christmasPuddingRecipe.addIngredient(Material.SUGAR);
        christmasPuddingRecipe.addIngredient(Material.SWEET_BERRIES);
        Bukkit.addRecipe(christmasPuddingRecipe);

        // Christmas Pudding Baking (Smoker)
        SmokingRecipe puddingSmoking = new SmokingRecipe(new NamespacedKey(ItemForge.getForge(), "pudding_smoking"), getChristmasPudding(), new RecipeChoice.ExactChoice(getUnmixedPudding()), 0.35f, 200);
        Bukkit.addRecipe(puddingSmoking);

        // Sweetened Almonds Recipe
        ShapelessRecipe sweetenedAlmondsRecipe = new ShapelessRecipe(new NamespacedKey(ItemForge.getForge(), "sweetened_almonds_recipe"), getSweetenedAlmonds());
        sweetenedAlmondsRecipe.addIngredient(Material.SWEET_BERRIES);
        sweetenedAlmondsRecipe.addIngredient(Material.SUGAR);
        Bukkit.addRecipe(sweetenedAlmondsRecipe);

        // Roasted Almonds Recipe (Campfire)
        CampfireRecipe roastedAlmondsCampfire = new CampfireRecipe(new NamespacedKey(ItemForge.getForge(), "roasted_almonds_campfire"), getRoastedAlmonds(), new RecipeChoice.ExactChoice(getSweetenedAlmonds()), 0.35f, 600);
        Bukkit.addRecipe(roastedAlmondsCampfire);
    }
}
