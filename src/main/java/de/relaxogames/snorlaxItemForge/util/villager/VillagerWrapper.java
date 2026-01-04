package de.relaxogames.snorlaxItemForge.util.villager;

import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.listener.villager.events.CustomVillagerWorkTickEvent;
import de.relaxogames.snorlaxItemForge.util.villager.villagertyps.Beekeeper;
import de.relaxogames.snorlaxItemForge.util.villager.villagertyps.Firecracker;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillagerWrapper {
    private static FileManager fM = new FileManager();

    private static final NamespacedKey PROFESSION_KEY = new NamespacedKey(ItemForge.getForge(), "villager_profession");
    private static final NamespacedKey BLOCK_BLOCKED_BY = new NamespacedKey(ItemForge.getForge(), "villager_uuid");

    private static final Map<UUID, CustomVillager> CACHE = new HashMap<>();

    public static CustomVillager from(Villager villager) {
        CustomVillager cached = CACHE.get(villager.getUniqueId());
        if (cached != null)
            return cached;

        if (villager.getProfession() != Villager.Profession.NONE) {
            // If it has a vanilla profession (including NITWIT), check if it's already one
            // of our custom professions
            if (!villager.getPersistentDataContainer().has(PROFESSION_KEY, PersistentDataType.STRING)) {
                return null;
            }
        }

        String type = villager.getPersistentDataContainer()
                .get(PROFESSION_KEY, PersistentDataType.STRING);

        if (type == null) {
            type = findNearestJob(villager.getLocation(), fM.villagerWorkingTableSearch());
        }

        // 🔒 ABSOLUT NOTWENDIG
        if (type == null) {
            return null;
        }

        CustomVillager cv = createInstance(type, villager);
        if (cv != null) {
            CACHE.put(villager.getUniqueId(), cv);
        }
        return cv;
    }

    public static CustomVillager load(Villager villager) {
        CustomVillager cached = CACHE.get(villager.getUniqueId());
        if (cached != null)
            return cached;

        String type = villager.getPersistentDataContainer()
                .get(PROFESSION_KEY, PersistentDataType.STRING);

        if (type == null) {
            return null;
        }

        CustomVillager cv = createInstance(type, villager);
        if (cv != null) {
            CACHE.put(villager.getUniqueId(), cv);
        }
        return cv;
    }

    private static CustomVillager createInstance(String type, Villager villager) {
        return switch (type) {
            case "beekeeper" -> new Beekeeper(villager);
            case "firecracker" -> new Firecracker(villager);
            default -> null;
        };
    }

    public static void uncache(UUID uuid) {
        CACHE.remove(uuid);
    }

    public static java.util.Collection<CustomVillager> getCachedVillagers() {
        return CACHE.values();
    }

    private static String findNearestJob(Location loc, int radius) {
        World world = loc.getWorld();
        if (world == null)
            return null;

        int startX = loc.getBlockX();
        int startY = loc.getBlockY();
        int startZ = loc.getBlockZ();

        double closestDistanceSq = Double.MAX_VALUE;
        String foundProfession = null;

        int actualRadius = Math.min(radius, 64);

        // Suche in Schichten von innen nach außen
        for (int r = 1; r <= actualRadius; r++) {
            boolean foundInThisLayer = false;

            for (int x = -r; x <= r; x++) {
                for (int y = -Math.min(r, 8); y <= Math.min(r, 8); y++) {
                    for (int z = -r; z <= r; z++) {
                        // Nur auf der Außenhülle des aktuellen Radius-Würfels suchen
                        if (Math.abs(x) != r && Math.abs(y) != r && Math.abs(z) != r)
                            continue;

                        Block block = world.getBlockAt(startX + x, startY + y, startZ + z);
                        Material type = block.getType();

                        if (type.isAir() || type == Material.GRASS_BLOCK || type == Material.DIRT)
                            continue;

                        CustomVillager.Profession prof = CustomVillager.Profession.convertBlockType(block.getType());
                        if (prof == null)
                            continue;

                        CustomBlockData data = new CustomBlockData(block, ItemForge.getForge());
                        if (data.has(BLOCK_BLOCKED_BY, PersistentDataType.STRING))
                            continue;

                        double distance = loc.distanceSquared(block.getLocation());
                        if (distance < closestDistanceSq) {
                            closestDistanceSq = distance;
                            foundProfession = prof.getKey().getKey();
                            foundInThisLayer = true;
                        }
                    }
                }
            }
            if (foundInThisLayer)
                return foundProfession;
        }
        return foundProfession;
    }

    private static boolean running = false;
    private static final Map<World, Long> lastBucket = new HashMap<>();

    public static void startWorkClock() {
        if (running)
            return;
        running = true;

        Bukkit.getScheduler().runTaskTimer(ItemForge.getForge(), () -> {
            for (World world : Bukkit.getWorlds()) {
                long time = world.getTime(); // 0–23999
                long bucket = time / 1000; // 0–23

                Long last = lastBucket.get(world);
                if (last == null || bucket != last) {
                    lastBucket.put(world, bucket);
                    Bukkit.getPluginManager().callEvent(
                            new CustomVillagerWorkTickEvent(world, time));
                }
            }
        }, 0L, 20L); // jede Sekunde
    }
}
