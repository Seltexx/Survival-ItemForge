package de.relaxogames.snorlaxItemForge.util.villager.villagertyps;

import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;

public class Firecracker extends CustomVillager {
    public Firecracker(Villager villager) {
        super(villager);
    }

    @Override
    public void acceptJob() {

        initializeTrades();
    }

    @Override
    public void workOnStation() {

    }

    @Override
    protected void initializeTrades() {

    }

    @Override
    public void replenishTrades() {

    }

    @Override
    public List<MerchantRecipe> buildMerchant() {
        return List.of();
    }
}
