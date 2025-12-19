package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.api.Lingo;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.api.interfaces.LingoUser;
import de.relaxogames.languages.Locale;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.util.CauldronManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;

public class LightConverter implements Listener {

    private final NamespacedKey itemFrameKey = new NamespacedKey(ItemForge.getForge(), "light_level");

    @EventHandler
    public void onInteractWithCauldron(PlayerInteractEvent e) {
        Player interacter = e.getPlayer();
        LingoUser lingoInteracter = new LingoPlayer(interacter.getUniqueId());

        ItemStack holdedStack = e.getItem();
        if (holdedStack == null || holdedStack.getItemMeta() == null) return;
        if (!holdedStack.getType().equals(Material.LIGHT)) return;
        if (interacter.isSneaking()) return;

        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null || !clickedBlock.getType().equals(Material.POWDER_SNOW_CAULDRON)) return;

        e.setCancelled(true);
        CauldronManager cauldron = new CauldronManager(clickedBlock, interacter);
        if (cauldron.getLevel() < 1) return;
        if (cauldron == null || !cauldron.hasData()) return;
        if (!cauldron.containsTincture())return;

        BlockDataMeta holdedBDM = (BlockDataMeta) holdedStack.getItemMeta();
        Light holdedLight = (Light) holdedBDM.getBlockData(Material.LIGHT);

        if (holdedLight.getLevel() == 15)return;

        int newLVL = holdedLight.getLevel() + 1;

        ItemStack lightItem = new ItemStack(Material.LIGHT, 1);
        BlockDataMeta meta = (BlockDataMeta) lightItem.getItemMeta();
        Light lightData = (Light) Bukkit.createBlockData(Material.LIGHT);
        lightData.setLevel(newLVL);
        meta.setCustomModelData(100);
        meta.setBlockData(lightData);
        meta.customName(Component.text(
                Lingo.getLibrary().getMessage(Locale.GERMAN, "Light-Block-Name").replace("{LEVEL}", String.valueOf(newLVL))
        ));
        lightItem.setItemMeta(meta);

        cauldron.depleteLevel();
        holdedStack.setAmount(holdedStack.getAmount() - 1);
        interacter.getInventory().addItem(lightItem);
    }
}
