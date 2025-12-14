package de.relaxogames.snorlaxItemForge.util.villager;

import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.util.villager.villagertyps.Beekeeper;
import de.relaxogames.snorlaxItemForge.util.villager.villagertyps.Firecracker;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

public class VillagerWrapper {
    private static FileManager fM = new FileManager();

    private static final NamespacedKey WORKING_TABLE_KEY = new NamespacedKey(ItemForge.getForge(), "working_station");
    private static final NamespacedKey PROFESSION_KEY = new NamespacedKey(ItemForge.getForge(), "villager_profession");
    private static final NamespacedKey BLOCK_BLOCKED_BY = new NamespacedKey(ItemForge.getForge(), "villager_uuid");

    public static CustomVillager from(Villager villager) {
        String type = villager.getPersistentDataContainer()
                .get(PROFESSION_KEY, PersistentDataType.STRING);

        if (type == null) {
            type = findNearestJob(villager.getLocation(), fM.villagerWorkingTableSearch());
        }

        // 🔒 ABSOLUT NOTWENDIG
        if (type == null) {
            return null;
        }

        return switch (type) {
            case "beekeeper" -> new Beekeeper(villager);
            case "firecracker" -> new Firecracker(villager);
            default -> null;
        };
    }


    private static String findNearestJob(Location loc, int radius) {
        double closestDistance = Double.MAX_VALUE;
        String foundProfession = null;

        World world = loc.getWorld();
        if (world == null) return null;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    Block block = world.getBlockAt(
                            loc.getBlockX() + x,
                            loc.getBlockY() + y,
                            loc.getBlockZ() + z
                    );

                    for (CustomVillager.Profession prof : CustomVillager.Profession.values()) {

                        if (block.getType() != prof.getWorkTable()) continue;

                        CustomBlockData data = new CustomBlockData(block, ItemForge.getForge());

                        if (data.has(BLOCK_BLOCKED_BY, PersistentDataType.STRING)) continue;

                        double distance = loc.distanceSquared(block.getLocation());

                        if (distance < closestDistance) {
                            closestDistance = distance;
                            foundProfession = prof.getKey().getKey();
                        }
                    }
                }
            }
        }
        return foundProfession;
    }

}
