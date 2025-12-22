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
        djManager.stopAndEject(e.getBlock(), e.getPlayer());
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent e){
        if (!(e.getBlock().getType().equals(Material.JUKEBOX)))return;
        djManager.stopAndEject(e.getBlock(), getNearestPlayer(e.getBlock().getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(PlayerInteractEvent e){
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))return;
        if (e.getClickedBlock() == null || !(e.getClickedBlock().getType().equals(Material.JUKEBOX)))return;
        
        CustomBlockData cbdJBX = new CustomBlockData(e.getClickedBlock(), ItemForge.getForge());
        if (!cbdJBX.has(PLAYING_KEY, PersistentDataType.INTEGER))return;

        djManager.stopAndEject(e.getClickedBlock(), e.getPlayer());
        e.setCancelled(true);
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

    private Player getNearestPlayer(Location location) {
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        if (location.getWorld() == null) return null;
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
