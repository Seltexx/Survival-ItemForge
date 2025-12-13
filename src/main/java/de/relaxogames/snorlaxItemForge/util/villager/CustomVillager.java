package de.relaxogames.snorlaxItemForge.util.villager;

import com.destroystokyo.paper.ParticleBuilder;
import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import net.kyori.adventure.text.Component;
import net.minecraft.world.level.pathfinder.Node;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.joml.Matrix2d;

import java.util.UUID;

public class CustomVillager {

    private FileManager fM = new FileManager();

    private Villager villager;
    private CraftVillager nmsVillager;
    private Block workingStation;
    private World currentWorld;
    private Profession profession;
    private Location locWork;

    private final NamespacedKey WORKING_TABLE_KEY = new NamespacedKey(ItemForge.getForge(), "working_station");
    private final NamespacedKey PROFESSION_KEY = new NamespacedKey(ItemForge.getForge(), "villager_profession");
    private final NamespacedKey BLOCK_BLOCKED_BY = new NamespacedKey(ItemForge.getForge(), "villager_uuid");

    public CustomVillager(Villager villager, Profession profession) {
        this.villager = villager;
        this.nmsVillager = (CraftVillager) villager;
        this.profession = profession;
    }

    public CustomVillager(Villager villager) {
        this.villager = villager;
        this.currentWorld = villager.getWorld();
        this.nmsVillager = (CraftVillager) villager;
    }

    public void walkTo(Location target) {
        nmsVillager.getHandle().getNavigation().moveTo(target.x(), target.y(), target.z(), 1);
    }

    public void walkToWorkStation() {
        nmsVillager.getHandle().getNavigation().moveTo(locWork.x(), locWork.y(), locWork.z(), 1);
    }

    public void setWorkingStation(Location location) {
        Block blockAT = location.getBlock();
        if (!blockAT.getType().equals(getProfession().getWorkTable())) return;
        this.locWork = location;
        this.workingStation = blockAT;
    }

    public Profession getProfession() {
        return profession;
    }

    public Location getWorkstationLocation() {
        String stored = villager.getPersistentDataContainer().get(
                WORKING_TABLE_KEY,
                PersistentDataType.STRING
        );

        if (stored != null) {
            String[] parts = stored.split(",");
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            double z = Double.parseDouble(parts[2]);
            float yaw = Float.parseFloat(parts[3]);
            float pitch = Float.parseFloat(parts[4]);

            return new Location(currentWorld, x, y, z, yaw, pitch);
        }
        return null;
    }

    public boolean moveToNearestWorkingStation() {
        if (!hasBlockInRange()) return false;
        Location nearest = findNearestBlock(villager.getLocation(), fM.villagerWorkingTableSearch());
        var nms = nmsVillager.getHandle();
        var navigator = nms.getNavigation(); // Navigation Zugriff

        // Pfad erstellen und starten
        var blockPos = new net.minecraft.core.BlockPos(nearest.getBlockX(), nearest.getBlockY(), nearest.getBlockZ());
        Block target = villager.getWorld().getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        var path = navigator.createPath(blockPos, 0);

        if (path != null) {
            navigator.moveTo(path, 0.5);
            CustomBlockData stationData = new CustomBlockData(target, ItemForge.getForge());

            String data = stationData.get(BLOCK_BLOCKED_BY, PersistentDataType.STRING);
            assert data != null;
            if (!UUID.fromString(data).equals(villager.getUniqueId()))return false;

            Location base = nearest.clone();

            // Schrittweite
            double step = 0.2;

            // Partikel
            ParticleBuilder particleBuilder = Particle.HAPPY_VILLAGER.builder();
            // Vier Seiten des Blocks markieren
            for (double i = 0; i <= 1; i += step) {

                // Unten (y = 0)
                particleBuilder.location(base.clone().add(i, 0, 0)).receivers(32, true).spawn();
                particleBuilder.location(base.clone().add(i, 0, 1)).receivers(32, true).spawn();
                particleBuilder.location(base.clone().add(0, 0, i)).receivers(32, true).spawn();
                particleBuilder.location(base.clone().add(1, 0, i)).receivers(32, true).spawn();

                // Oben (y = 1)
                particleBuilder.location(base.clone().add(i, 1, 0)).receivers(32, true).spawn();
                particleBuilder.location(base.clone().add(i, 1, 1)).receivers(32, true).spawn();
                particleBuilder.location(base.clone().add(0, 1, i)).receivers(32, true).spawn();
                particleBuilder.location(base.clone().add(1, 1, i)).receivers(32, true).spawn();

                // Vertikale Kanten
                particleBuilder.location(base.clone().add(0, i, 0)).receivers(32, true).spawn();
                particleBuilder.location(base.clone().add(1, i, 0)).receivers(32, true).spawn();
                particleBuilder.location(base.clone().add(0, i, 1)).receivers(32, true).spawn();
                particleBuilder.location(base.clone().add(1, i, 1)).receivers(32, true).spawn();
            }

            if (navigator.getPath().isDone()) {
                locWork = nearest;
                workingStation = nearest.getBlock();
                stationData.set(BLOCK_BLOCKED_BY, PersistentDataType.STRING, villager.getUniqueId().toString());
                villager.getPersistentDataContainer().set(PROFESSION_KEY, PersistentDataType.STRING, Profession.convertBlockType(workingStation.getType()).getKey().getKey());
                villager.getPersistentDataContainer().set(WORKING_TABLE_KEY, PersistentDataType.STRING, locWork.toString());
                villager.setGlowing(true);
                villager.setProfession(Villager.Profession.CLERIC);
                return true;
            }
        }
        return false;
    }

    public Block pathGoal() {
        Node endNode = nmsVillager.getHandle().getNavigation().getPath().getEndNode();
        assert endNode != null;
        Location pathEnd = new Location(villager.getWorld(), endNode.x, endNode.y - 1, endNode.z);

        return pathEnd.getBlock();
    }

    public boolean hasBlockInRange() {
        return findNearestBlock(villager.getLocation(), fM.villagerWorkingTableSearch()) != null;
    }

    public boolean hasPath() {
        return nmsVillager.getHandle().getNavigation().getPath() != null;
    }

    private Location findNearestBlock(Location loc, int radius) {
        double closestDistance = Double.MAX_VALUE;
        Location closest = null;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    Block block = loc.getWorld().getBlockAt(
                            loc.getBlockX() + x,
                            loc.getBlockY() + y,
                            loc.getBlockZ() + z
                    );

                    // Blocktyp prüfen
                    for (Profession prof : Profession.values()) {
                        if (block.getType() != prof.getWorkTable()) continue;

                        // CustomBlockData nur erzeugen, wenn Block passt
                        CustomBlockData data = new CustomBlockData(block, ItemForge.getForge());

                        // Block muss frei sein
                        if (data.has(BLOCK_BLOCKED_BY, PersistentDataType.STRING)) continue;

                        double distance = loc.distanceSquared(block.getLocation());
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closest = block.getLocation();
                        }
                    }
                }
            }
        }
        return closest;
    }

    public void removeWorkingstation() {
        profession = null;
        locWork = null;
        workingStation = null;
        villager.getPersistentDataContainer().remove(WORKING_TABLE_KEY);
        villager.getPersistentDataContainer().remove(PROFESSION_KEY);

        ParticleBuilder particleBuilder = Particle.DUST.builder()
                .color(Color.BLUE, 2.0f);
        for (double i = 2; i <= 1.0; i += 0.25) {
            particleBuilder.location(villager.getLocation().clone().add(0, i, 0)).receivers(32, true).spawn();
        }
    }

    static enum Profession {

        BEEKEEPER("beekeeper", Material.BEEHIVE),
        FIRECRACKER("firecracker", Material.CRAFTER);

        private final NamespacedKey key;
        private final Material workTable;

        Profession(String id, Material workTable) {
            this.key = new NamespacedKey(ItemForge.getForge(), id);
            this.workTable = workTable;
        }

        public Material getWorkTable() {
            return workTable;
        }

        public NamespacedKey getKey() {
            return key;
        }

        public static Profession convertBlockType(Material material) {
            for (Profession professions : Profession.values()) {
                if (!professions.getWorkTable().equals(material)) continue;
                return professions;
            }
            return null;
        }
    }
}



