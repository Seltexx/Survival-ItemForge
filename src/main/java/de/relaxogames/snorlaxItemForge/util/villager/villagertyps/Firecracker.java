package de.relaxogames.snorlaxItemForge.util.villager.villagertyps;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.relaxogames.api.Lingo;
import de.relaxogames.languages.Locale;
import de.relaxogames.languages.ServerColors;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.util.ItemBuilder;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Firecracker extends CustomVillager {
    private Merchant merchant;
    private List<MerchantRecipe> merchantRecipes;

    public Firecracker(Villager villager) {
        super(villager, Component.text(
                Lingo.getLibrary().getMessage(Locale.GERMAN, "Firecracker-Title")
        ).color(ServerColors.Red3.color()));
        merchant = getMerchant();
        Bukkit.getPluginManager().registerEvents(new FirecrackerListener(), ItemForge.getForge());

    }

    @Override
    public void acceptJob() {
        villager.setProfession(Villager.Profession.LEATHERWORKER);
        villager.setVillagerType(Villager.Type.SAVANNA);
        villager.setVillagerLevel(1);
        villager.setRestocksToday(2);
        update(villager);

        villager.getEquipment().setHelmet(getFirecrackerHead());
        villager.getEquipment().setHelmetDropChance(0.0F);
        initializeTrades();
    }

    @Override
    protected void workOnStation() {
        getCurrentWorld().playSound(getVillager().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 1);
        getCurrentWorld().playSound(getWorkstationLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1F, 2);
        Random random = new Random();
        int count = 30;

        for (int i = 0; i < count; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 3; // -1.5 bis 1.5
            double offsetY = random.nextDouble() * 2;         // 0 bis 2
            double offsetZ = (random.nextDouble() - 0.5) * 3; // -1.5 bis 1.5

            getWorkstationLocation().getWorld().spawnParticle(
                    Particle.END_ROD,
                    getWorkstationLocation().clone().add(0.5, 1, 0.5).add(offsetX, offsetY, offsetZ),
                    1,
                    0, 0, 0, 0 // keine extra Geschwindigkeit nötig
            );
        }
        replenishTrades();
    }

    @Override
    protected void initializeTrades() {
        Random random = new Random();

        List<MerchantRecipe> trades = new ArrayList<>(importTrades());
        int level = getLevel().getNmsLevel();

        // LEVEL 1 → 2 Trades
        if (level >= 1 && trades.size() < 2) {

            if (random.nextBoolean()) {
                MerchantRecipe sugarTrade = new MerchantRecipe(new ItemStack(Material.EMERALD, random.nextBoolean() ? 1 : 2), 8);
                sugarTrade.addIngredient(new ItemStack(Material.SUGAR, random.nextInt(7, 17)));
                trades.add(sugarTrade);
            } else {
                MerchantRecipe honeyTrade =
                        new MerchantRecipe(new ItemStack(Material.EMERALD, 1), 8);
                honeyTrade.addIngredient(
                        new ItemStack(Material.HONEY_BOTTLE, random.nextInt(6, 12)));
                trades.add(honeyTrade);
            }
        }

        // LEVEL 2 → 4 Trades
        if (level >= 2 && trades.size() < 4) {

            MerchantRecipe beeHiveTrade = new MerchantRecipe(new ItemStack(Material.BEEHIVE, 1), 8);
            beeHiveTrade.addIngredient(new ItemStack(Material.EMERALD, random.nextInt(5, 11)));
            trades.add(beeHiveTrade);

            MerchantRecipe honeyComb = new MerchantRecipe(new ItemStack(Material.EMERALD, 1), 8);
            honeyComb.addIngredient(new ItemStack(Material.HONEYCOMB, random.nextInt(3, 5)));
            trades.add(honeyComb);
        }

        // LEVEL 3 → 6 Trades
        if (level >= 3 && trades.size() < 6) {

            if (random.nextBoolean()) {

                MerchantRecipe honeyCombBlockTrade = new MerchantRecipe(new ItemStack(Material.HONEYCOMB_BLOCK, 1), 8);
                honeyCombBlockTrade.addIngredient(new ItemStack(Material.HONEYCOMB, 3));
                honeyCombBlockTrade.addIngredient(new ItemStack(Material.EMERALD, 1));
                trades.add(honeyCombBlockTrade);
            } else {
                List<Component> lore = new ArrayList<>();
                for (int d = 1; d <= 2; d++) {
                    String key = "Item-Kaolin-Lore-" + d;
                    String message = Lingo.getLibrary().getMessage(Locale.GERMAN, key);
                    lore.add(Component.text(message));
                }
                ItemBuilder sugar = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Item-Kaolin-Name"))
                        .color(NamedTextColor.WHITE), Material.SUGAR, lore);

                ItemStack kaolin = sugar.getItem();
                ItemMeta meta = kaolin.getItemMeta();
                meta.setCustomModelData(12);
                kaolin.setItemMeta(meta);
                MerchantRecipe saccarinTrade = new MerchantRecipe(kaolin, 8);
                saccarinTrade.addIngredient(new ItemStack(Material.SUGAR, 1));
                saccarinTrade.addIngredient(new ItemStack(Material.HONEY_BOTTLE, 2));
                trades.add(saccarinTrade);
            }
            MerchantRecipe bannerPatternTrade = new MerchantRecipe(new ItemStack(Material.FLOWER_BANNER_PATTERN, 1), 8);
            bannerPatternTrade.addIngredient(new ItemStack(Material.EMERALD, random.nextInt(2, 3)));
            trades.add(bannerPatternTrade);
        }

        // LEVEL 4 → 8 Trades
        if (level >= 4 && trades.size() < 8) {
            MerchantRecipe bannerTrade = new MerchantRecipe(new ItemStack(Material.EMERALD, 2), 8);
            bannerTrade.addIngredient(new ItemStack(Material.ORANGE_BANNER, 1));
            trades.add(bannerTrade);

            MerchantRecipe campfireTrade = new MerchantRecipe(new ItemStack(Material.EMERALD, random.nextInt(2, 3)), 8);
            campfireTrade.addIngredient(new ItemStack(Material.CAMPFIRE, 1));
            trades.add(campfireTrade);

        }

        // LEVEL 5 → 10 Trades
        if (level >= 5 && trades.size() < 10) {

            String key = "Beeworker-Honey-";

            ItemStack honey = null;
            int price = 10;
            switch (random.nextInt(1, 4)) {
                case 1: {
                    key = key + "Worst";
                    ItemBuilder honeyBottle = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Name"))
                            .color(NamedTextColor.GOLD), Material.HONEY_BOTTLE, List.of(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, key))));

                    honey = honeyBottle.getItem();
                    ItemMeta meta = honey.getItemMeta();
                    meta.setCustomModelData(111);
                    meta.setMaxStackSize(16);
                    honey.setItemMeta(meta);
                    honey.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
                    honey.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    price = random.nextInt(3, 4);
                    break;
                }

                case 2: {
                    key = key + "Bad";
                    ItemBuilder honeyBottle = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Name"))
                            .color(NamedTextColor.GOLD), Material.HONEY_BOTTLE, List.of(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, key))));

                    honey = honeyBottle.getItem();
                    ItemMeta meta = honey.getItemMeta();
                    meta.setCustomModelData(222);
                    meta.setMaxStackSize(16);
                    honey.setItemMeta(meta);
                    honey.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
                    honey.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    price = random.nextInt(4, 5);
                    break;
                }

                case 3: {
                    key = key + "Okay";
                    ItemBuilder honeyBottle = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Name"))
                            .color(NamedTextColor.GOLD), Material.HONEY_BOTTLE, List.of(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, key))));

                    honey = honeyBottle.getItem();
                    ItemMeta meta = honey.getItemMeta();
                    meta.setCustomModelData(333);
                    meta.setMaxStackSize(16);
                    honey.setItemMeta(meta);
                    honey.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
                    honey.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    price = random.nextInt(5, 6);
                    break;
                }

                case 4: {
                    key = key + "Best";
                    ItemBuilder honeyBottle = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Name"))
                            .color(NamedTextColor.GOLD), Material.HONEY_BOTTLE, List.of(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, key))));

                    honey = honeyBottle.getItem();
                    ItemMeta meta = honey.getItemMeta();
                    meta.setCustomModelData(444);
                    meta.setMaxStackSize(16);
                    honey.setItemMeta(meta);
                    honey.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
                    honey.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    price = random.nextInt(5, 7);
                    break;
                }

            }

            if (honey == null) honey = new ItemStack(Material.POPPY);
            MerchantRecipe honeyTrade = new MerchantRecipe(honey, 4);
            honeyTrade.addIngredient(new ItemStack(Material.EMERALD, price));
            trades.add(honeyTrade);
        }

        reimportTrades(trades);
        villager.setRecipes(trades);
    }

    @Override
    public void replenishTrades() {
        restock();
    }

    @Override
    public List<MerchantRecipe> buildMerchant() {
        return merchantRecipes;
    }

    private ItemStack getFirecrackerHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.getProperties().add(new ProfileProperty(
                "textures",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBhMTQzMjNiYTkzNGE0N2Q5NzY4OWUxMzZkN2IzYzZhODI2ZTgzNDMxZjBkNzRkZDY5NThlNzY3NWQ1YTkifX19"
        ));
        headMeta.setPlayerProfile(profile);
        head.setItemMeta(headMeta);
        return head;
    }

    static class FirecrackerListener implements Listener {

    }
}