package de.relaxogames.snorlaxItemForge.listener.musicdiscs;

import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MusicListener implements Listener {

    DJManager djManager = new DJManager();

    private final NamespacedKey PLAYING_KEY = new NamespacedKey(ItemForge.getForge(), "currently_playing");

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player interacter = e.getPlayer();
        ItemStack holdInHand = e.getItem();
        if (holdInHand == null)return;
        if (holdInHand.getItemMeta() == null || !holdInHand.getItemMeta().hasCustomModelData())return;

        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))return;
        if (!e.getClickedBlock().getType().equals(Material.JUKEBOX))return;
        CustomBlockData cbdClicked = new CustomBlockData(e.getClickedBlock(), ItemForge.getForge());
        if (cbdClicked == null)return;
        e.setCancelled(true);
        if (cbdClicked.has(PLAYING_KEY))return;


        List<Player> inRange = new ArrayList<>();
        inRange.add(interacter);

        for (Player p : Bukkit.getOnlinePlayers()){
            if (p.getLocation().distance(e.getClickedBlock().getLocation()) > 48 || inRange.contains(p))continue;
            inRange.add(p);
        }

        if (inRange.isEmpty())return;
        MusicDiscs disc = MusicDiscs.fromCustomModelData(holdInHand.getItemMeta().getCustomModelData());
        if (disc == null)return;
        djManager.playSong(disc, e.getClickedBlock(), inRange);
    }

}
