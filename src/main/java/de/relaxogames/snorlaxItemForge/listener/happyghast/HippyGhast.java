package de.relaxogames.snorlaxItemForge.listener.happyghast;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface HippyGhast {

    /* -------------------- ENTITY -------------------- */

    HappyGhast getEntity();

    /* -------------------- SPEED -------------------- */

    double getSpeed();
    void applySpeed(double speed, int durationTicks);
    void removeSpeed();

    /* -------------------- DURATION -------------------- */

    int getDuration();        // verbleibende Ticks
    int getStartDuration();   // Start-Ticks

    /* -------------------- RIDERS -------------------- */

    List<UUID> getRider();
    void addRider(Player player);
    void removeRider(Player player);
    boolean isRider(Player player);

    /* -------------------- UI -------------------- */

    BossBar bossbar();

    /* -------------------- CLEANUP -------------------- */

    void remove();
}
