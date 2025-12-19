package de.relaxogames.snorlaxItemForge.listener.musicdiscs;

import com.jeff_media.customblockdata.CustomBlockData;
import com.xxmicloxx.NoteBlockAPI.event.SongDestroyingEvent;
import com.xxmicloxx.NoteBlockAPI.event.SongStoppedEvent;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import de.relaxogames.api.Lingo;
import de.relaxogames.languages.Locale;
import de.relaxogames.snorlaxItemForge.ItemForge;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class JukeboxListener implements Listener {

    private DJManager djManager = new DJManager();

    private final NamespacedKey PLAYING_KEY = new NamespacedKey(ItemForge.getForge(), "currently_playing");

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if (!(e.getBlock().getType().equals(Material.JUKEBOX)))return;
        Jukebox jukebox = (Jukebox) e.getBlock();
        CustomBlockData cbdJBX = new CustomBlockData(jukebox.getBlock(), ItemForge.getForge());
        if (cbdJBX == null)return;
        if (!cbdJBX.has(PLAYING_KEY, PersistentDataType.INTEGER))return;

        dropDisc(jukebox.getLocation());

        djManager.stopSong(e.getPlayer(), jukebox.getLocation());
        jukebox.stopPlaying();
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent e){
        if (!(e.getBlock().getType().equals(Material.JUKEBOX)))return;
        Jukebox jukebox = (Jukebox) e.getBlock();
        CustomBlockData cbdJBX = new CustomBlockData(jukebox.getBlock(), ItemForge.getForge());
        if (cbdJBX == null)return;
        if (!cbdJBX.has(PLAYING_KEY, PersistentDataType.INTEGER))return;

        dropDisc(jukebox.getLocation());

        djManager.stopSong(getNearestPlayer(jukebox.getLocation()), jukebox.getLocation());
        jukebox.stopPlaying();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))return;
        if (!(e.getClickedBlock().getType().equals(Material.JUKEBOX)))return;
        Jukebox jukebox = (Jukebox) e.getClickedBlock();
        CustomBlockData cbdJBX = new CustomBlockData(jukebox.getBlock(), ItemForge.getForge());
        if (cbdJBX == null)return;
        if (!cbdJBX.has(PLAYING_KEY, PersistentDataType.INTEGER))return;

        dropDisc(jukebox.getLocation());

        djManager.stopSong(e.getPlayer(), jukebox.getLocation());
        jukebox.stopPlaying();
    }

    @EventHandler
    public void onDestroy(SongDestroyingEvent e){
        SongPlayer sp = e.getSongPlayer();
        if (!(sp instanceof PositionSongPlayer psp))return;
        CustomBlockData cbdJBX = new CustomBlockData(psp.getTargetLocation().getBlock(), ItemForge.getForge());
        if (cbdJBX== null)return;
        cbdJBX.remove(PLAYING_KEY);
    }

    @EventHandler
    public void onSongStopped(SongStoppedEvent e){
        SongPlayer sp = e.getSongPlayer();
        if (!(sp instanceof PositionSongPlayer psp))return;
        CustomBlockData cbdJBX = new CustomBlockData(psp.getTargetLocation().getBlock(), ItemForge.getForge());
        if (cbdJBX== null)return;
        cbdJBX.remove(PLAYING_KEY);
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
