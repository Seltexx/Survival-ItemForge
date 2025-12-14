package de.relaxogames.snorlaxItemForge.listener;

import de.relaxogames.api.Lingo;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.api.interfaces.LingoUser;
import de.relaxogames.languages.ServerColors;
import de.relaxogames.snorlaxItemForge.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EntityKillListener implements Listener {

    @EventHandler
    public void onKill(EntityDeathEvent e){
        Player killerPlayer = e.getEntity().getKiller();
        if (killerPlayer == null)return;
        LingoUser lingoKiller = new LingoPlayer(killerPlayer.getUniqueId());
        Entity killed = e.getEntity();

        if (!killed.getType().equals(EntityType.GHAST))return;

        Random random = new Random();
        if (random.nextInt(100) >= 40){
            List<Component> lore = new ArrayList<>();
            for (int d = 1; d <= 2; d++) {
                String key = "Item-Kaolin-Lore-" + d;
                String message = Lingo.getLibrary().getMessage(lingoKiller.getLanguage(), key);
                lore.add(Component.text(message));
            }

            ItemBuilder sugar = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(lingoKiller.getLanguage(), "Item-Kaolin-Name"))
                    .color(NamedTextColor.WHITE), Material.SUGAR, lore);

            ItemStack kaolin = sugar.getItem();
            ItemMeta meta = kaolin.getItemMeta();
            meta.setCustomModelData(12);
            kaolin.setItemMeta(meta);

            killed.getWorld().dropItem(killed.getLocation(), kaolin);
        }
    }
}
