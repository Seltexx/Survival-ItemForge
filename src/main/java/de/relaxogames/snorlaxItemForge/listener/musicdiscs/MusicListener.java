package de.relaxogames.snorlaxItemForge.listener.musicdiscs;

import com.jeff_media.customblockdata.CustomBlockData;
import com.xxmicloxx.NoteBlockAPI.event.SongEndEvent;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import org.bukkit.Bukkit;
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
import org.bukkit.block.Jukebox;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MusicListener implements Listener {

    DJManager djManager = new DJManager();

    private final NamespacedKey PLAYING_KEY = new NamespacedKey(ItemForge.getForge(), "currently_playing");

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e){
        Player interacter = e.getPlayer();
        ItemStack holdInHand = e.getItem();
        if (holdInHand == null)return;
        if (holdInHand.getItemMeta() == null || !holdInHand.getItemMeta().hasCustomModelData())return;

        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))return;
        if (e.getClickedBlock() == null || !e.getClickedBlock().getType().equals(Material.JUKEBOX))return;
        
        MusicDiscs disc = MusicDiscs.fromCustomModelData(holdInHand.getItemMeta().getCustomModelData());
        if (disc == null)return;

        e.setCancelled(true);

        CustomBlockData cbdClicked = new CustomBlockData(e.getClickedBlock(), ItemForge.getForge());
        if (cbdClicked.has(PLAYING_KEY)) {
            djManager.stopAndEject(e.getClickedBlock(), interacter);
            return;
        }

        if (e.getClickedBlock().getState() instanceof Jukebox jukebox) {
            if (jukebox.hasRecord()) {
                jukebox.eject();
                return;
            }
        }

        List<Player> inRange = new ArrayList<>();
        inRange.add(interacter);

        for (Player p : Bukkit.getOnlinePlayers()){
            if (!p.getWorld().equals(e.getClickedBlock().getWorld())) continue;
            if (p.getLocation().distance(e.getClickedBlock().getLocation()) > 48 || inRange.contains(p))continue;
            inRange.add(p);
        }

        if (inRange.isEmpty())return;
        djManager.playSong(disc, e.getClickedBlock(), inRange);
        holdInHand.setAmount(holdInHand.getAmount() - 1);
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
            Advancements.playout(p, disc.getAdvancement());
        }
    }

}
