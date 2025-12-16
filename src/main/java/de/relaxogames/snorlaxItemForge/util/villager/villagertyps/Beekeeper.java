package de.relaxogames.snorlaxItemForge.util.villager.villagertyps;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.relaxogames.api.Lingo;
import de.relaxogames.languages.Locale;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Beekeeper extends CustomVillager {

    private Merchant merchant;
    private List<MerchantRecipe> merchantRecipes;

    public Beekeeper(Villager villager) {
        super(villager, Component.text(
                Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Title")
        ).color(TextColor.color(255, 128, 0)));
        merchant = getMerchant();
    }

    @Override
    public void acceptJob() {
        villager.setProfession(Villager.Profession.LEATHERWORKER);
        villager.setVillagerType(Villager.Type.SNOW);
        villager.setVillagerLevel(1);
        villager.setRestocksToday(2);
        update();

        villager.getEquipment().setHelmet(getBeekeeperHead());
        villager.getEquipment().setHelmetDropChance(0.0F);
        initializeTrades();
    }

    @Override
    public void workOnStation() {
        getCurrentWorld().playSound(getWorkstationLocation(), Sound.BLOCK_BEEHIVE_SHEAR, 100,2);
        getCurrentWorld().playSound(getVillager().getLocation(), Sound.ENTITY_VILLAGER_YES, 100, 1);
        Particle.ENTITY_EFFECT.builder()
                .location(getVillager().getLocation())
                .offset(0.5, 1, 0.5)
                .count(20)
                .data(Color.fromARGB(200, 255, 128, 0))
                .receivers(32, true)
                .spawn();
        replenishTrades();
    }

    @Override
    protected void initializeTrades() {
        Random random = new Random();

        //NOVICE
        switch (getLevel().getNmsLevel()) {
            case 5:{
                merchant.setRecipe(5, rollFlowerRecipe());

                MerchantRecipe retradeSugar = new MerchantRecipe(new ItemStack(Material.EMERALD, random.nextInt(3)), 16);
                retradeSugar.addIngredient(new ItemStack(Material.SUGAR, random.nextInt(7, 17)));

                merchant.setRecipe(6, retradeSugar);
            }
            case 4:{
                merchant.setRecipe(4, rollFlowerRecipe());

                MerchantRecipe retradeSugar = new MerchantRecipe(new ItemStack(Material.EMERALD, random.nextInt(3)), 16);
                retradeSugar.addIngredient(new ItemStack(Material.SUGAR, random.nextInt(7, 17)));

                merchant.setRecipe(5, retradeSugar);
            }
            case 3:{
                merchant.setRecipe(3, rollFlowerRecipe());

                MerchantRecipe retradeSugar = new MerchantRecipe(new ItemStack(Material.EMERALD, random.nextInt(3)), 16);
                retradeSugar.addIngredient(new ItemStack(Material.SUGAR, random.nextInt(7, 17)));

                merchant.setRecipe(4, retradeSugar);
            }
            case 2:{
                merchant.setRecipe(2, rollFlowerRecipe());

                MerchantRecipe retradeSugar = new MerchantRecipe(new ItemStack(Material.EMERALD, random.nextInt(3)), 16);
                retradeSugar.addIngredient(new ItemStack(Material.SUGAR, random.nextInt(7, 17)));

                merchant.setRecipe(3, retradeSugar);
            }
            case 1:{
                merchant.setRecipe(0, rollFlowerRecipe());

                MerchantRecipe retradeSugar = new MerchantRecipe(new ItemStack(Material.EMERALD, random.nextBoolean() ? 1 : 2), 16);
                retradeSugar.addIngredient(new ItemStack(Material.SUGAR, random.nextInt(7, 17)));

                merchant.setRecipe(1, retradeSugar);
            }
        }
        merchantRecipes = merchant.getRecipes();
        exportTrades(merchantRecipes);
    }

    @Override
    public void replenishTrades() {
        restock();
    }

    @Override
    public List<MerchantRecipe> buildMerchant() {
        return merchantRecipes;
    }

    private ItemStack getBeekeeperHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.getProperties().add(new ProfileProperty(
                "textures",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjQ5NWM1ZTNiZTFiODAyZjQ1N2I2ZjljMmU1NGE5OWEwZmFlNWM4NTU2N2IzOTQ3NDg5MmVmOGY4YmE2N2RiZiJ9fX0="
        ));
        headMeta.setPlayerProfile(profile);
        head.setItemMeta(headMeta);
        return head;
    }

    private MerchantRecipe rollFlowerRecipe(){
        Random random = new Random();
        int balance = random.nextInt(15, 64);
        MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(Material.EMERALD, 1), 16);
        switch (random.nextInt(10)){
            case 1: {
                merchantRecipe.addIngredient(new ItemStack(Material.SUNFLOWER, balance));
                break;
            }
            case 2: {
                merchantRecipe.addIngredient(new ItemStack(Material.OXEYE_DAISY, balance));
                break;
            }
            case 3: {
                merchantRecipe.addIngredient(new ItemStack(Material.LILY_OF_THE_VALLEY, balance));
                break;
            }
            case 4: {
                merchantRecipe.addIngredient(new ItemStack(Material.DANDELION, balance));
                break;
            }
            case 5: {
                merchantRecipe.addIngredient(new ItemStack(Material.POPPY, balance));
                break;
            }
            case 6: {
                merchantRecipe.addIngredient(new ItemStack(Material.BLUE_ORCHID, balance));
                break;
            }
            case 7: {
                merchantRecipe.addIngredient(new ItemStack(Material.ALLIUM, balance));
                break;
            }
            case 8: {
                merchantRecipe.addIngredient(new ItemStack(Material.AZURE_BLUET, balance));
                break;
            }
            case 9: {
                merchantRecipe.addIngredient(new ItemStack(Material.RED_TULIP, balance));
                break;
            }
        }
        return merchantRecipe;
    }
}
