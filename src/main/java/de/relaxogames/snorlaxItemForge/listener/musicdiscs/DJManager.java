package de.relaxogames.snorlaxItemForge.listener.musicdiscs;

import com.jeff_media.customblockdata.CustomBlockData;
import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.List;

public class DJManager {

    private FileManager fm = new FileManager();

    private final NamespacedKey PLAYING_KEY = new NamespacedKey(ItemForge.getForge(), "currently_playing");

    public void playSong(MusicDiscs music, Block jukebx, List<Player> hearer){
        CustomBlockData cbdJBX = new CustomBlockData(jukebx, ItemForge.getForge());
        cbdJBX.set(PLAYING_KEY, PersistentDataType.INTEGER, music.getCustomModelData());


        Song song = NBSDecoder.parse(new File(FileManager.getDiscoFolder() + "//" + music.getFile() + ".nbs"));

        PositionSongPlayer psp = new PositionSongPlayer(song);
        psp.setTargetLocation(jukebx.getLocation());
        psp.setDistance(fm.jukeboxMaxDistance());
        for (Player there : hearer){
            psp.addPlayer(there);
        }
        psp.setAutoDestroy(true);
        psp.setPlaying(true);
    }

    public void stopSong(Player player, Location location){
        for (SongPlayer all : NoteBlockAPI.getSongPlayersByPlayer(player)){
            if (!(all instanceof PositionSongPlayer psp))continue;
            if (psp.getTargetLocation().distance(location) > 0.5)continue;
            psp.setPlaying(false);
            psp.destroy();
        }
    }

    public void stopAndEject(Block jkbx, Player player) {
        CustomBlockData cbdJBX = new CustomBlockData(jkbx, ItemForge.getForge());
        Integer fromPDC = cbdJBX.get(PLAYING_KEY, PersistentDataType.INTEGER);
        if (fromPDC != null) {
            jkbx.getWorld().dropItemNaturally(jkbx.getLocation().add(0.5, 0.5, 0.5), MusicDiscs.convertModelIdToItemStack(fromPDC));
            cbdJBX.remove(PLAYING_KEY);
        }
        stopSong(player, jkbx.getLocation());
        if (jkbx.getState() instanceof org.bukkit.block.Jukebox jukebox) {
            jukebox.stopPlaying();
        }
    }

}
