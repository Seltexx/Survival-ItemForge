package de.relaxogames.snorlaxItemForge.listener.happyghast;

import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CustomGhast implements HippyGhast {

    private final HappyGhast happyGhast;
    private final BossBar bossBar;

    private BukkitTask task;

    private int durationTicks;
    private int startDurationTicks;

    private final Set<UUID> riders = new HashSet<>();

    private static final NamespacedKey SPEED_KEY =
            new NamespacedKey(ItemForge.getForge(), "speed");
    private static final NamespacedKey START_DURATION =
            new NamespacedKey(ItemForge.getForge(), "start_duration");
    private static final NamespacedKey CURRENT_DURATION =
            new NamespacedKey(ItemForge.getForge(), "current_duration");
    private static final NamespacedKey TASK_KEY =
            new NamespacedKey(ItemForge.getForge(), "speed_task_id");

    public CustomGhast(HappyGhast ghast) {
        this.happyGhast = ghast;

        NamespacedKey key =
                new NamespacedKey(ItemForge.getForge(), ghast.getUniqueId().toString());

        BossBar existing = Bukkit.getBossBar(key);
        this.bossBar = existing != null
                ? existing
                : Bukkit.createBossBar(
                key,
                "Happy Ghast Boost",
                BarColor.GREEN,
                BarStyle.SOLID
        );

        bossBar.setVisible(true);
        restore();
    }

    /* -------------------- ENTITY -------------------- */

    @Override
    public HappyGhast getEntity() {
        return happyGhast;
    }

    /* -------------------- SPEED -------------------- */

    @Override
    public double getSpeed() {
        return happyGhast.getPersistentDataContainer()
                .getOrDefault(SPEED_KEY, PersistentDataType.DOUBLE, 0.05);
    }

    @Override
    public void applySpeed(double speed, int durationTicks) {
        if (task != null)reset();
        this.startDurationTicks = durationTicks;
        this.durationTicks = durationTicks;

        PersistentDataContainer pdc = happyGhast.getPersistentDataContainer();
        pdc.set(SPEED_KEY, PersistentDataType.DOUBLE, speed);
        pdc.set(START_DURATION, PersistentDataType.INTEGER, startDurationTicks);
        pdc.set(CURRENT_DURATION, PersistentDataType.INTEGER, durationTicks);

        happyGhast.getAttribute(Attribute.FLYING_SPEED).setBaseValue(speed);
        startScheduler();
    }

    @Override
    public void removeSpeed() {
        stopScheduler();

        happyGhast.getAttribute(Attribute.FLYING_SPEED).setBaseValue(0.05);
        happyGhast.getPersistentDataContainer()
                .set(SPEED_KEY, PersistentDataType.DOUBLE, 0.05);

        bossBar.setTitle("§o§7Happy Ghast Boost ausgelaufen!");
        bossBar.setColor(BarColor.WHITE);
        bossBar.setProgress(0);
    }

    /* -------------------- DURATION -------------------- */

    @Override
    public int getDuration() {
        return durationTicks;
    }

    @Override
    public int getStartDuration() {
        return startDurationTicks;
    }

    /* -------------------- TIMER -------------------- */

    private void startScheduler() {
        // Prüfen, ob bereits eine Task existiert
        Integer existingTaskId = happyGhast.getPersistentDataContainer().get(TASK_KEY, PersistentDataType.INTEGER);
        if (existingTaskId != null) {
            Bukkit.getScheduler().cancelTask(existingTaskId);
        }

        // Falls keine Task existiert, erstelle neue
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                ItemForge.getForge(),
                this::tick,
                0L,
                20L
        );

        happyGhast.getPersistentDataContainer().set(TASK_KEY, PersistentDataType.INTEGER, task.getTaskId());
    }

    private void stopScheduler() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void tick() {
        if (!happyGhast.isValid()
                || !happyGhast.getLocation().isChunkLoaded()) {
            stopScheduler();
            return;
        }

        if (riders.isEmpty()) return; // ⏸ pausiert

        if (durationTicks <= 0) {
            removeSpeed();
            return;
        }

        durationTicks--;

        happyGhast.getPersistentDataContainer()
                .set(CURRENT_DURATION, PersistentDataType.INTEGER, durationTicks);

        // Fortschritt für BossBar
        double progress = (double) durationTicks / startDurationTicks;
        bossBar.setProgress(progress);

        updateColor(progress);
        updateTitle();
    }

    private void updateTitle(){
        // --- Hier wird Tick zu mm:ss ---
        int totalSeconds = durationTicks;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String time = String.format("%02d:%02d", minutes, seconds);

        String name = happyGhast.customName() == null ? "Happy Ghast" : happyGhast.getCustomName();

        switch (bossBar.getColor()){
            case BarColor.RED -> {
                bossBar.setTitle("§c" + name + " Boost §7• §4" + time);
                break;
            }
            case BarColor.YELLOW -> {
                bossBar.setTitle("§e" + name + " Boost §7• §6" + time);
                break;
            }
            case BarColor.GREEN -> {
                bossBar.setTitle("§a" + name + " Boost §7• §2" + time);
                break;
            }
            default -> {
                bossBar.setTitle("§b" + name + " Boost §7• §3" + time);
                break;
            }
        }
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
    public List<Entity> getRider() {
        return happyGhast.getPassengers();
    }

    @Override
    public void addRider(Player player) {
        if (!riders.add(player.getUniqueId())) return;

        bossBar.addPlayer(player);
        startScheduler();
    }

    @Override
    public void removeRider(Player player) {
        riders.remove(player.getUniqueId());
        bossBar.removePlayer(player);
    }

    @Override
    public boolean isRider(Player player) {
        return riders.contains(player.getUniqueId());
    }

    /* -------------------- UI -------------------- */

    @Override
    public BossBar bossbar() {
        return bossBar;
    }

    /* -------------------- PERSISTENCE -------------------- */

    private void restore() {
        PersistentDataContainer pdc = happyGhast.getPersistentDataContainer();

        Integer start = pdc.get(START_DURATION, PersistentDataType.INTEGER);
        Integer current = pdc.get(CURRENT_DURATION, PersistentDataType.INTEGER);

        if (start != null && current != null) {
            startDurationTicks = start;
            durationTicks = current;
        }

        Double speed = pdc.get(SPEED_KEY, PersistentDataType.DOUBLE);
        if (speed != null) {
            happyGhast.getAttribute(Attribute.FLYING_SPEED).setBaseValue(speed);
        }
    }

    /* -------------------- CLEANUP -------------------- */

    @Override
    public void remove() {
        stopScheduler();
        bossBar.removeAll();
        Bukkit.removeBossBar(
                new NamespacedKey(ItemForge.getForge(),
                        happyGhast.getUniqueId().toString())
        );
    }

    private void reset(){
        removeSpeed();
        stopScheduler();
        bossBar.setProgress(1);
        durationTicks = 0;
        startDurationTicks = 0;
        remove();
    }
}
