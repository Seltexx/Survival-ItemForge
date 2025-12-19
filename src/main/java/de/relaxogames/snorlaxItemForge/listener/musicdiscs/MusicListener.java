package de.relaxogames.snorlaxItemForge.listener.musicdiscs;

import com.jeff_media.customblockdata.CustomBlockData;
import com.xxmicloxx.NoteBlockAPI.event.PlayerRangeStateChangeEvent;
import com.xxmicloxx.NoteBlockAPI.event.SongEndEvent;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MusicListener implements Listener {

    DJManager djManager = new DJManager();
    FileManager fileManager = new FileManager();

    private final NamespacedKey PLAYING_KEY = new NamespacedKey(ItemForge.getForge(), "currently_playing");
    private final NamespacedKey IS_ACTIVE = new NamespacedKey(ItemForge.getForge(), "disc_is_playing");

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e){
        Player interacter = e.getPlayer();
        ItemStack holdInHand = e.getItem();
        if (holdInHand == null)return;
        if (holdInHand.getItemMeta() == null || !holdInHand.getItemMeta().hasCustomModelData())return;

        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))return;
        if (!e.getClickedBlock().getType().equals(Material.JUKEBOX))return;
        CustomBlockData cbdClicked = new CustomBlockData(e.getClickedBlock(), ItemForge.getForge());
        if (cbdClicked == null)return;
        if (e.getItem().getType().isRecord() && cbdClicked.has(IS_ACTIVE, PersistentDataType.BOOLEAN) && cbdClicked.get(IS_ACTIVE, PersistentDataType.BOOLEAN)){
            e.setCancelled(true);
            return;
        }
        e.setCancelled(true);
        if (cbdClicked.has(IS_ACTIVE, PersistentDataType.BOOLEAN) && cbdClicked.get(IS_ACTIVE, PersistentDataType.BOOLEAN))return;
        if (cbdClicked.has(PLAYING_KEY))dropDisc(e.getClickedBlock().getLocation());

        List<Player> inRange = new ArrayList<>();
        inRange.add(interacter);

        for (Player p : Bukkit.getOnlinePlayers()){
            if (p.getLocation().distance(e.getClickedBlock().getLocation()) > fileManager.jukeboxMaxDistance() || inRange.contains(p))continue;
            inRange.add(p);
        }

        if (inRange.isEmpty())return;
        MusicDiscs disc = MusicDiscs.fromCustomModelData(holdInHand.getItemMeta().getCustomModelData());
        if (disc == null)return;
        e.getItem().setAmount(e.getItem().getAmount()-1);
        djManager.playSong(disc, e.getClickedBlock(), inRange);
    }

    private void dropDisc(Location jkbx){
        CustomBlockData cbdJBX = new CustomBlockData(jkbx.getBlock(), ItemForge.getForge());
        Location itemDrop = jkbx.add(0, 0.5, 0);
        Integer fromPDC = cbdJBX.get(PLAYING_KEY, PersistentDataType.INTEGER);
        if (fromPDC == null)return;
        itemDrop.getWorld().dropItem(itemDrop, MusicDiscs.convertModelIdToItemStack(fromPDC));
        cbdJBX.remove(PLAYING_KEY);
    }

    @EventHandler
    public void onEnd(SongEndEvent e){
        SongPlayer sp = e.getSongPlayer();
        if (!(sp instanceof PositionSongPlayer psp))return;
        CustomBlockData cbdJBX = new CustomBlockData(psp.getTargetLocation().getBlock(), ItemForge.getForge());
        if (cbdJBX== null)return;
        for (UUID uuid : e.getSongPlayer().getPlayerUUIDs()){
            Player p = Bukkit.getPlayer(uuid);
            if (p == null)continue;
            MusicDiscs disc = MusicDiscs.fromCustomModelData(cbdJBX.get(PLAYING_KEY, PersistentDataType.INTEGER));
            if (disc == null)return;
            //Advancements.playout(p, disc.getAdvancement());
            Advancements.playout(p, Advancement.SONGS_LITTLE_AMADEUS, disc.getAdvancement().getTrigger());
        }
    }

    @EventHandler
    public void onGoInArea(PlayerRangeStateChangeEvent e){
        if (e.isInRange())e.getSongPlayer().addPlayer(e.getPlayer());
    }

}
