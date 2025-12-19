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
import org.bukkit.block.data.type.Light;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.FireworkMeta;
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

            if (random.nextBoolean()){
                MerchantRecipe slimeTrade = new MerchantRecipe(new ItemStack(Material.SLIME_BALL, 2), 8);
                slimeTrade.addIngredient(new ItemStack(Material.EMERALD, random.nextInt(2, 4)));
                trades.add(slimeTrade);
            }else {
                MerchantRecipe fireworkTrade = new MerchantRecipe(new ItemStack(Material.FIREWORK_ROCKET, 3), 8);
                fireworkTrade.addIngredient(new ItemStack(Material.EMERALD, 2));
                trades.add(fireworkTrade);

            }

            if (random.nextBoolean()) {
                MerchantRecipe gunpowederTrade = new MerchantRecipe(new ItemStack(Material.EMERALD, random.nextBoolean() ? 1 : 2), 16);
                gunpowederTrade.addIngredient(new ItemStack(Material.GUNPOWDER, random.nextInt(16, 32)));
                trades.add(gunpowederTrade);
            } else {
                MerchantRecipe redstoneTrade = new MerchantRecipe(new ItemStack(Material.EMERALD, 8), 8);
                redstoneTrade.addIngredient(new ItemStack(Material.REDSTONE, random.nextInt(16, 20)));
                trades.add(redstoneTrade);
            }
        }

        // LEVEL 2 → 4 Trades
        if (level >= 2 && trades.size() < 4) {

            ItemStack lightItem = new ItemStack(Material.LIGHT, 1);
            BlockDataMeta meta = (BlockDataMeta) lightItem.getItemMeta();
            Light lightData = (Light) Bukkit.createBlockData(Material.LIGHT);
            lightData.setLevel(0);
            meta.setCustomModelData(100);
            meta.setBlockData(lightData);
            meta.customName(Component.text(
                    Lingo.getLibrary().getMessage(Locale.GERMAN, "Light-Block-Name").replace("{LEVEL}", String.valueOf(0))
            ));
            lightItem.setItemMeta(meta);

            MerchantRecipe lightTrade = new MerchantRecipe(lightItem, 8);
            lightTrade.addIngredient(new ItemStack(Material.EMERALD, random.nextInt(2, 11)));
            trades.add(lightTrade);

            MerchantRecipe tripWireHookTrade = new MerchantRecipe(new ItemStack(Material.EMERALD, 1), 16);
            tripWireHookTrade.addIngredient(new ItemStack(Material.TRIPWIRE_HOOK, random.nextInt(6, 8)));
            trades.add(tripWireHookTrade);
        }

        // LEVEL 3 → 6 Trades
        if (level >= 3 && trades.size() < 6) {

            if (random.nextBoolean()){
                MerchantRecipe paperTrade = new MerchantRecipe(new ItemStack(Material.EMERALD), 16);
                paperTrade.addIngredient(new ItemStack(Material.PAPER, random.nextInt(8, 16)));
                trades.add(paperTrade);
            }else {
                MerchantRecipe paperTrade = new MerchantRecipe(new ItemStack(Material.EMERALD, 3), 16);
                paperTrade.addIngredient(new ItemStack(Material.FIREWORK_STAR, random.nextInt(2, 4)));
                trades.add(paperTrade);
            }

            MerchantRecipe discTrade = new MerchantRecipe(rollRandomDisc(), 1);
            discTrade.addIngredient(new ItemStack(Material.EMERALD, random.nextInt(23, 48)));
            trades.add(discTrade);
        }
              // LEVEL 4 → 8 Trades
          if (level >= 4 && trades.size() < 8) {
              /// AMMOOOO
              List<Component> ammoLore = new ArrayList<>();
              ammoLore.add(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Head-Drop-Ammunition-Lore")));
              ItemBuilder ammonation = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Head-Drop-Ammunition-Name"))
                      .color(ServerColors.Red3.color()), Material.FIREWORK_ROCKET, ammoLore);

              ItemStack ammoFirework = ammonation.getItem();
              ItemMeta ammoMeta = ammoFirework.getItemMeta();
              ammoMeta.setCustomModelData(2006);
              ammoFirework.setItemMeta(ammoMeta);
              ammoFirework.setAmount(2);

              MerchantRecipe ammoTrade = new MerchantRecipe(ammoFirework, 8);
              ammoTrade.addIngredient(new ItemStack(Material.EMERALD, random.nextInt(4, 6)));
              ammoTrade.addIngredient(new ItemStack(Material.FIREWORK_ROCKET, 2));
              trades.add(ammoTrade);

              /// BOOOK
              List<Component> lore = new ArrayList<>();
              lore.add(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Head-Drop-Book-Lore")));
              ItemBuilder enchantedBook = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Head-Drop-Book-Name"))
                      .color(NamedTextColor.WHITE), Material.ENCHANTED_BOOK, lore);

              ItemStack executionerBook = enchantedBook.getItem();
              ItemMeta meta = executionerBook.getItemMeta();
              meta.setCustomModelData(80);
              executionerBook.setItemMeta(meta);

              MerchantRecipe bookTrade = new MerchantRecipe(executionerBook, 8);
              bookTrade.addIngredient(new ItemStack(Material.EMERALD, random.nextInt(16, 64)));
              bookTrade.addIngredient(new ItemStack(Material.BOOK, 1));
              trades.add(bookTrade);

          }

        // LEVEL 5 → 10 Trades
        if (level >= 5 && trades.size() < 9) {
            ItemStack fireWork4 = new ItemStack(Material.FIREWORK_ROCKET);
            FireworkMeta power4Meta = (FireworkMeta) fireWork4.getItemMeta();
            power4Meta.setPower(4);
            power4Meta.setCustomModelData(4);
            fireWork4.setItemMeta(power4Meta);

            ItemStack fireWork3 = new ItemStack(Material.FIREWORK_ROCKET, 4);
            FireworkMeta power3Meta = (FireworkMeta) fireWork3.getItemMeta();
            power3Meta.setPower(3);
            fireWork3.setItemMeta(power3Meta);

            MerchantRecipe ammoTrade = new MerchantRecipe(fireWork4, 16);
            ammoTrade.addIngredient(new ItemStack(Material.EMERALD, random.nextInt(4, 16)));
            ammoTrade.addIngredient(fireWork3);
            trades.add(ammoTrade);
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

    public ItemStack rollRandomDisc(){
        Random random = new Random();

        Material discMat = Material.MUSIC_DISC_5;
        int discID = -1;
        String discKey = "";
        List<Component> lore = new ArrayList<>();


        switch (random.nextInt(1, 6)){

            //WENN HIER WAS ANGEPASST WIRD UNBEDINGT IM JUKEBOXLISTENER getByID auch ändern!
            case 1:{
                discMat = Material.MUSIC_DISC_CHIRP;
                discID = 1978;
                discKey = "YMCA";
                lore.add(Component.text(
                        Lingo.getLibrary().getMessage(Locale.GERMAN, "Music-Disc-YMCA-Lore")
                ));
                break;
            }
            case 2:{
                discMat = Material.MUSIC_DISC_11;
                discID = 1808;
                discKey = "BH-5";
                lore.add(Component.text(
                        Lingo.getLibrary().getMessage(Locale.GERMAN, "Music-Disc-BH-5-Lore")
                ));
                break;
            }
            case 3:{
                discMat = Material.MUSIC_DISC_RELIC;
                discID = 2020;
                discKey = "GF";
                lore.add(Component.text(
                        Lingo.getLibrary().getMessage(Locale.GERMAN, "Music-Disc-GF-Lore")
                ));
                break;
            }
            case 4:{
                discMat = Material.MUSIC_DISC_PIGSTEP;
                discID = 1970;
                discKey = "GRIECHISCH";
                lore.add(Component.text(
                        Lingo.getLibrary().getMessage(Locale.GERMAN, "Music-Disc-GRIECHISCH-Lore")
                ));
                break;
            }
            case 5:{
                discMat = Material.MUSIC_DISC_LAVA_CHICKEN;
                discID = 1960;
                discKey = "RICKROLL";
                lore.add(Component.text(
                        Lingo.getLibrary().getMessage(Locale.GERMAN, "Music-Disc-RICKROLL-Lore")
                ));
                break;
            }
            case 6:{
                discMat = Material.MUSIC_DISC_TEARS;
                discID = 241225;
                discKey = "CHRISTMAS-25";
                lore.add(Component.text(
                        Lingo.getLibrary().getMessage(Locale.GERMAN, "Music-Disc-CHRISTMAS-25-Lore")
                ));
                break;
            }
        }

        ItemStack musicDisco = new ItemStack(discMat, 1);
        ItemMeta meta = musicDisco.getItemMeta();
        meta.setCustomModelData(discID);
        meta.lore(lore);
        meta.customName(Component.text(
                Lingo.getLibrary().getMessage(Locale.GERMAN, "Music-Disc-"+ discKey +"-Name")
        ));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);
        musicDisco.setItemMeta(meta);
        return musicDisco;
    }

    static class FirecrackerListener implements Listener {

    }
}