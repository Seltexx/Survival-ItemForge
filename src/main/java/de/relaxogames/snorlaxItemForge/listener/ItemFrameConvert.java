package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.api.Lingo;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.api.interfaces.LingoUser;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.util.CauldronManager;
import de.relaxogames.snorlaxItemForge.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemFrameConvert implements Listener {

    private final NamespacedKey itemFrameKey = new NamespacedKey(ItemForge.getForge(), "is_invisible");

    @EventHandler
    public void onInteractWithCauldron(PlayerInteractEvent e){
        Player interacter = e.getPlayer();
        LingoUser lingoInteracter = new LingoPlayer(interacter.getUniqueId());

        ItemStack holdedStack = e.getItem();
        if (holdedStack == null || holdedStack.getItemMeta() == null)return;
        if (holdedStack.getItemMeta().hasCustomModelData())return;
        if (!holdedStack.getType().equals(Material.ITEM_FRAME) || !holdedStack.getType().equals(Material.GLOW_ITEM_FRAME))return;

        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null || !clickedBlock.getType().equals(Material.POWDER_SNOW_CAULDRON))return;
        CauldronManager cauldron = new CauldronManager(clickedBlock, interacter);
        if (cauldron.getLevel() < 1)return;

        if (cauldron == null || !cauldron.hasData())return;

        List<Component> lore = new ArrayList<>();
        for (int d = 1; d <= 23; d++) {
            lore.add(Component.text(Lingo.getLibrary().getMessage(lingoInteracter.getLanguage(), "Item-Itemframe-Lore-" + d)));
        }

        ItemStack converted = new ItemBuilder(
                Component.text(Lingo.getLibrary().getMessage(lingoInteracter.getLanguage(), "Item-Itemframe-Name")),
                e.getItem().getType(),
                lore
        ).getItem();

        converted.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
        converted.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        converted.getItemMeta().getPersistentDataContainer().set(itemFrameKey, PersistentDataType.BOOLEAN, true);
        converted.getItemMeta().setCustomModelData(35);

        holdedStack.setAmount(holdedStack.getAmount()-1);
        interacter.getInventory().addItem(converted);

        clickedBlock.getWorld().playSound(clickedBlock.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1000, 1.3F);

        cauldron.depleteLevel();
    }

}
