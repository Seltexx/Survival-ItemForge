package de.relaxogames.snorlaxItemForge.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillListener implements Listener {

    @EventHandler
    public void onKill(PlayerDeathEvent e){
        Player killedPlayer = e.getPlayer();
        Player killer = e.getPlayer().getKiller();
    }

}
