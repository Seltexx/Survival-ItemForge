package de.relaxogames.snorlaxItemForge.util.villager;

import com.destroystokyo.paper.ParticleBuilder;
import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import net.minecraft.world.level.pathfinder.Node;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class CustomVillager {

    protected final FileManager fM = new FileManager();
    protected final Villager villager;
    protected final CraftVillager nmsVillager;
    protected final World currentWorld;

    protected Block workingStation;
    protected Location locWork;
    protected Profession profession;

    protected static final NamespacedKey WORKING_TABLE_KEY =
            new NamespacedKey(ItemForge.getForge(), "working_station");
    protected static final NamespacedKey PROFESSION_KEY =
            new NamespacedKey(ItemForge.getForge(), "villager_profession");
    protected static final NamespacedKey BLOCK_BLOCKED_BY =
            new NamespacedKey(ItemForge.getForge(), "villager_uuid");

    public CustomVillager(Villager villager) {
        this.villager = villager;
        this.currentWorld = villager.getWorld();
        this.nmsVillager = (CraftVillager) villager;
        loadPersistent();
        moveToNearestWorkingStation();
    }

    protected void loadPersistent() {
        String stored = villager.getPersistentDataContainer()
                .get(WORKING_TABLE_KEY, PersistentDataType.STRING);

        if (stored != null) {
            String[] p = stored.split(",");
            if (p.length == 5) {
                locWork = new Location(
                        currentWorld,
                        Double.parseDouble(p[0]),
                        Double.parseDouble(p[1]),
                        Double.parseDouble(p[2]),
                        Float.parseFloat(p[3]),
                        Float.parseFloat(p[4])
                );
                workingStation = locWork.getBlock();
            }
        }

        String prof = villager.getPersistentDataContainer()
                .get(PROFESSION_KEY, PersistentDataType.STRING);

        if (prof != null) {
            for (Profession p : Profession.values()) {
                if (p.getKey().getKey().equals(prof)) {
                    profession = p;
                    break;
                }
            }
        }
    }

    public boolean walkTo(Location target, double speed, boolean work) {
        var navigator = nmsVillager.getHandle().getNavigation();
        navigator.stop();
        navigator.moveTo(target.getX(), target.getY(), target.getZ(), speed);

        final boolean[] done = {false};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (navigator.isStuck() || villager.isInsideVehicle()) {
                    navigator.stop();
                    cancel();
                    done[0] = false;
                    return;
                }

                if (villager.getLocation().distanceSquared(target) <= 1.5) {
                    navigator.stop();
                    cancel();
                    if (work) workOnStation();
                    done[0] = true;
                    return;
                }

                navigator.moveTo(target.getX(), target.getY(), target.getZ(), speed);
            }
        }.runTaskTimer(ItemForge.getForge(), 0L, 5L);
        return done[0];
    }

    public void setWorkingStation(Location location) {
        if (location == null) return;
        Block block = location.getBlock();
        if (getProfession() == null) return;
        if (block.getType() != getProfession().getWorkTable()) return;
        locWork = location;
        workingStation = block;
    }

    public Profession getProfession() {
        return profession;
    }

    public Location getWorkstationLocation() {
        return locWork;
    }

    public boolean moveToOwnWorkingStation() {
        if (locWork == null) return false;
        return walkTo(locWork, fM.villagerWalkingSpeed(), false);
    }

    public void work(){
        if (locWork == null)return;
        walkTo(locWork, fM.villagerWalkingSpeed(), true);
    }

    public boolean moveToNearestWorkingStation() {
        if (!villager.isAdult()) return false;
        if (locWork != null) return false;

        Location nearest = findNearestBlock(
                villager.getLocation(),
                fM.villagerWorkingTableSearch()
        );

        if (nearest == null) return false;

        Block block = nearest.getBlock();
        Profession prof = Profession.convertBlockType(block.getType());
        if (prof == null) return false;

        CustomBlockData data = new CustomBlockData(block, ItemForge.getForge());
        if (data.has(BLOCK_BLOCKED_BY, PersistentDataType.STRING)) return false;

        data.set(BLOCK_BLOCKED_BY, PersistentDataType.STRING,
                villager.getUniqueId().toString());

        profession = prof;
        locWork = nearest;
        workingStation = block;

        villager.getPersistentDataContainer().set(
                WORKING_TABLE_KEY,
                PersistentDataType.STRING,
                serialize(nearest)
        );

        villager.getPersistentDataContainer().set(
                PROFESSION_KEY,
                PersistentDataType.STRING,
                prof.getKey().getKey()
        );
        ParticleBuilder particleBuilder = Particle.HAPPY_VILLAGER.builder();

        Location base = block.getLocation();

        // Höhe der Umrandung (z. B. Oberkante)
        double y = base.getY() + 1.01;

        // Schrittweite
        double step = 0.2;

        for (double i = 0; i <= 1; i += step) {

            // Vorderseite (Z = 0)
            particleBuilder.location(base.clone().add(i, y - base.getY(), 0))
                    .receivers(32, true)
                    .spawn();

            // Rückseite (Z = 1)
            particleBuilder.location(base.clone().add(i, y - base.getY(), 1))
                    .receivers(32, true)
                    .spawn();

            // Linke Seite (X = 0)
            particleBuilder.location(base.clone().add(0, y - base.getY(), i))
                    .receivers(32, true)
                    .spawn();

            // Rechte Seite (X = 1)
            particleBuilder.location(base.clone().add(1, y - base.getY(), i))
                    .receivers(32, true)
                    .spawn();
        }

        //HINLAUFEN
        var navigator = nmsVillager.getHandle().getNavigation();
        navigator.stop();
        navigator.moveTo(nearest.getX(), nearest.getY(), nearest.getZ(), fM.villagerSprintingSpeed());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (navigator.isStuck() || villager.isInsideVehicle()) {
                    navigator.stop();
                    cancel();
                    removeWorkingstation();
                    return;
                }

                if (villager.getLocation().distanceSquared(nearest) <= 2.5) {
                    navigator.stop();
                    cancel();
                    acceptJob();
                    return;
                }

                navigator.moveTo(nearest.getX(), nearest.getY(), nearest.getZ(), fM.villagerSprintingSpeed());
            }
        }.runTaskTimer(ItemForge.getForge(), 0L, 5L);
        return true;
    }

    public abstract void acceptJob();
    protected abstract void workOnStation();
    public abstract void replenishTrades();

    public boolean hasBlockInRange() {
        return findNearestBlock(
                villager.getLocation(),
                fM.villagerWorkingTableSearch()
        ) != null;
    }

    public boolean hasPath() {
        return nmsVillager.getHandle().getNavigation().getPath() != null;
    }

    protected Location findNearestBlock(Location loc, int radius) {
        double closest = Double.MAX_VALUE;
        Location result = null;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    Block block = loc.getWorld().getBlockAt(
                            loc.getBlockX() + x,
                            loc.getBlockY() + y,
                            loc.getBlockZ() + z
                    );

                    Profession p = Profession.convertBlockType(block.getType());
                    if (p == null) continue;

                    CustomBlockData data = new CustomBlockData(block, ItemForge.getForge());
                    if (data.has(BLOCK_BLOCKED_BY, PersistentDataType.STRING)) continue;

                    double d = loc.distanceSquared(block.getLocation());
                    if (d < closest) {
                        closest = d;
                        result = block.getLocation();
                    }
                }
            }
        }
        return result;
    }

    public void removeWorkingstation() {
        profession = null;
        locWork = null;
        workingStation = null;

        villager.getPersistentDataContainer().remove(WORKING_TABLE_KEY);
        villager.getPersistentDataContainer().remove(PROFESSION_KEY);
        villager.getEquipment().clear();
        villager.setProfession(Villager.Profession.NONE);
    }

    protected String serialize(Location loc) {
        return loc.getX() + "," +
                loc.getY() + "," +
                loc.getZ() + "," +
                loc.getYaw() + "," +
                loc.getPitch();
    }

    public World getCurrentWorld() {
        return currentWorld;
    }

    public Villager getVillager() {
        return villager;
    }

    public Block getWorkingStation() {
        return workingStation;
    }

    public enum Profession {

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
            for (Profession p : values()) {
                if (p.workTable == material) return p;
            }
            return null;
        }

        public boolean searchAndAssignWorkstation(CustomVillager cv) {
            return cv.moveToNearestWorkingStation();
        }
    }
}
