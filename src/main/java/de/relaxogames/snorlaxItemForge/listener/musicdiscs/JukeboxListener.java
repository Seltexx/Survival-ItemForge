package de.relaxogames.snorlaxItemForge.listener.musicdiscs;

import com.jeff_media.customblockdata.CustomBlockData;
import com.xxmicloxx.NoteBlockAPI.event.SongDestroyingEvent;
import com.xxmicloxx.NoteBlockAPI.event.SongStoppedEvent;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class JukeboxListener implements Listener {

    private DJManager djManager = new DJManager();

    private final NamespacedKey PLAYING_KEY = new NamespacedKey(ItemForge.getForge(), "currently_playing");
    private final NamespacedKey IS_ACTIVE = new NamespacedKey(ItemForge.getForge(), "disc_is_playing");

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if (!(e.getBlock().getType().equals(Material.JUKEBOX)))return;
        Block jukebox = e.getBlock();
        CustomBlockData cbdJBX = new CustomBlockData(jukebox, ItemForge.getForge());
        if (cbdJBX == null)return;
        if (!cbdJBX.has(PLAYING_KEY, PersistentDataType.INTEGER))return;

        dropDisc(jukebox.getLocation());

        djManager.stopSong(e.getPlayer(), jukebox.getLocation());
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent e){
        if (!(e.getExplodedBlockState().getType().equals(Material.JUKEBOX)))return;
        Block jukebox = e.getExplodedBlockState().getBlock();
        CustomBlockData cbdJBX = new CustomBlockData(jukebox, ItemForge.getForge());
        if (cbdJBX == null)return;
        if (!cbdJBX.has(PLAYING_KEY, PersistentDataType.INTEGER))return;

        dropDisc(jukebox.getLocation());

        djManager.stopSong(getNearestPlayer(jukebox.getLocation()), jukebox.getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onClick(PlayerInteractEvent e){
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))return;
        if (!(e.getClickedBlock().getType().equals(Material.JUKEBOX)))return;
        if (e.getItem() != null && e.getItem().hasItemMeta() &&
                e.getItem().getItemMeta().hasCustomModelData() &&
                e.getItem().getType().isRecord())if (MusicDiscs.convertModelIdToItemStack(e.getItem().getItemMeta().getCustomModelData()) != null)return;
        Block jukebox = e.getClickedBlock();
        CustomBlockData cbdJBX = new CustomBlockData(jukebox, ItemForge.getForge());
        if (cbdJBX == null)return;
        if (!cbdJBX.has(IS_ACTIVE, PersistentDataType.BOOLEAN))return;
        if (!cbdJBX.has(PLAYING_KEY, PersistentDataType.INTEGER))return;

        dropDisc(jukebox.getLocation());

        djManager.stopSong(e.getPlayer(), jukebox.getLocation());
    }

    @EventHandler
    public void onDestroy(SongDestroyingEvent e){
        SongPlayer sp = e.getSongPlayer();
        if (!(sp instanceof PositionSongPlayer psp))return;
        CustomBlockData cbdJBX = new CustomBlockData(psp.getTargetLocation().getBlock(), ItemForge.getForge());
        if (cbdJBX== null)return;
        cbdJBX.set(IS_ACTIVE, PersistentDataType.BOOLEAN, false);
    }

    @EventHandler
    public void onSongStopped(SongStoppedEvent e){
        SongPlayer sp = e.getSongPlayer();
        if (!(sp instanceof PositionSongPlayer psp))return;
        CustomBlockData cbdJBX = new CustomBlockData(psp.getTargetLocation().getBlock(), ItemForge.getForge());
        if (cbdJBX== null)return;
        cbdJBX.set(IS_ACTIVE, PersistentDataType.BOOLEAN, false);
    }

    private void dropDisc(Location jkbx){
        CustomBlockData cbdJBX = new CustomBlockData(jkbx.getBlock(), ItemForge.getForge());
        Location itemDrop = jkbx.add(0, 0.5, 0);
        Integer fromPDC = cbdJBX.get(PLAYING_KEY, PersistentDataType.INTEGER);
        if (fromPDC == null)return;
        itemDrop.getWorld().dropItem(itemDrop, MusicDiscs.convertModelIdToItemStack(fromPDC));
        cbdJBX.remove(PLAYING_KEY);
    }

    private Player getNearestPlayer(Location location) {
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Player player : location.getWorld().getPlayers()) {
            double distance = player.getLocation().distanceSquared(location);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = player;
            }
        }
        return nearest;
    }
}
