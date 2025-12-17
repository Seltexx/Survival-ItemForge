package de.relaxogames.snorlaxItemForge.listener.happyghast;

import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CustomGhast implements HippyGhast {

    private final HappyGhast happyGhast;
    private BossBar bossBar;
    private BukkitTask task;

    private int duration;
    private int startDuration;

    private final List<UUID> riders = new ArrayList<>();

    private static final NamespacedKey SPEED_KEY =
            new NamespacedKey(ItemForge.getForge(), "speed");

    private static final NamespacedKey START_DURATION =
            new NamespacedKey(ItemForge.getForge(), "start_duration");

    private static final NamespacedKey CURRENT_DURATION =
            new NamespacedKey(ItemForge.getForge(), "current_duration");

    public CustomGhast(HappyGhast ghast) {
        this.happyGhast = ghast;

        this.bossBar = Bukkit.getBossBar(new NamespacedKey(ItemForge.getForge(), ghast.getUniqueId().toString()));
        if (bossBar == null){
            bossBar = Bukkit.createBossBar(
                    new NamespacedKey(ItemForge.getForge(), ghast.getUniqueId().toString()),
                    "Happy Ghast",
                    BarColor.GREEN,
                    BarStyle.SOLID
            );
        }

        restore();
    }

    /* -------------------- CORE -------------------- */

    @Override
    public HappyGhast getEntity() {
        return happyGhast;
    }

    @Override
    public double getSpeed() {
        return happyGhast.getPersistentDataContainer().get(SPEED_KEY, PersistentDataType.DOUBLE);
    }

    @Override
    public BossBar bossbar() {
        return bossBar;
    }

    /* -------------------- SPEED -------------------- */

    @Override
    public void applySpeed(double speed, int ticks) {
        PersistentDataContainer pdc = happyGhast.getPersistentDataContainer();

        this.startDuration = ticks*20;
        this.duration = ticks*20;

        pdc.set(SPEED_KEY, PersistentDataType.DOUBLE, speed);
        pdc.set(START_DURATION, PersistentDataType.INTEGER, ticks);
        pdc.set(CURRENT_DURATION, PersistentDataType.INTEGER, ticks);

        happyGhast.getAttribute(Attribute.FLYING_SPEED).setBaseValue(speed);
        bossBar.setVisible(true);

        startScheduler();
    }

    @Override
    public void removeSpeed() {
        happyGhast.getAttribute(Attribute.FLYING_SPEED).setBaseValue(0.05);
        happyGhast.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.05);

        bossBar.setTitle("Happy Ghast Boost abgelaufen!");
        bossBar.setColor(BarColor.RED);
        bossBar.setProgress(0);
        bossBar.setVisible(false);
        stopScheduler();
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getStartDuration() {
        return startDuration;
    }

    /* -------------------- TIMER -------------------- */

    private void startScheduler() {
        if (task != null) return;

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                ItemForge.getForge(),
                this::tick,
                1L,
                20
        );
    }

    private void stopScheduler() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void tick() {
        if (!happyGhast.isValid()
                || !happyGhast.getLocation().isChunkLoaded()
                || riders.isEmpty()) {
            stopScheduler();
            return;
        }

        if (duration <= 0) {
            removeSpeed();
            return;
        }

        duration--;

        happyGhast.getPersistentDataContainer()
                .set(CURRENT_DURATION, PersistentDataType.INTEGER, duration);

        double progress = (double) duration / startDuration;
        bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));

        updateColor(progress);
        updateTitle();
    }

    /* -------------------- UI -------------------- */

    private void updateTitle() {
        int seconds = duration / 20;
        bossBar.setTitle("Happy Ghast Boost • " + seconds + " sek");
    }

    private void updateColor(double progress) {
        if (progress > 0.66) {
            bossBar.setColor(BarColor.GREEN);
        } else if (progress > 0.33) {
            bossBar.setColor(BarColor.YELLOW);
        } else {
            bossBar.setColor(BarColor.RED);
        }
    }

    /* -------------------- RIDERS -------------------- */

    @Override
    public void addRider(Player player) {
        if (riders.contains(player.getUniqueId())) return;

        riders.add(player.getUniqueId());
        bossBar.addPlayer(player);
        startScheduler();
    }

    @Override
    public void removeRider(Player player) {
        bossBar.removePlayer(player);
        riders.remove(player.getUniqueId());

        if (riders.isEmpty()) {
            stopScheduler();
        }
    }

    @Override
    public boolean isRider(Player player) {
        return riders.contains(player.getUniqueId());
    }

    @Override
    public List<UUID> getRider() {
        return riders;
    }

    /* -------------------- PERSISTENCE -------------------- */

    private void restore() {
        PersistentDataContainer pdc = happyGhast.getPersistentDataContainer();

        Integer start = pdc.get(START_DURATION, PersistentDataType.INTEGER);
        Integer current = pdc.get(CURRENT_DURATION, PersistentDataType.INTEGER);

        if (start == null || current == null) return;

        this.startDuration = start;
        this.duration = current;

        Double speed = pdc.get(SPEED_KEY, PersistentDataType.DOUBLE);
        if (speed != null) {
            happyGhast.getAttribute(Attribute.FLYING_SPEED).setBaseValue(speed);
        }
    }

    @Override
    public void remove() {
        stopScheduler();
        bossBar.removeAll();
        bossBar.setVisible(false);
        Bukkit.removeBossBar(
                new NamespacedKey(ItemForge.getForge(), happyGhast.getUniqueId().toString())
        );
    }
}
