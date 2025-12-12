package de.relaxogames.snorlaxItemForge.util.villager;

import de.relaxogames.snorlaxItemForge.ItemForge;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.persistence.PersistentDataType;

public class Villager {

    private org.bukkit.entity.Villager villager;
    private CraftVillager nmsVillager;
    private Block workingStation;
    private World currentWorld;
    private Profession profession;
    private Location locWork;

    private final NamespacedKey WORKING_TABLE_KEY = new NamespacedKey(ItemForge.getForge(), "working_station");
    private final NamespacedKey PROFESSION_KEY = new NamespacedKey(ItemForge.getForge(), "villager_profession");

    public Villager(org.bukkit.entity.Villager villager, Block workingStation) {
        this.villager = villager;
        this.workingStation = workingStation;
        this.nmsVillager = (CraftVillager) villager;
    }

    public Villager(org.bukkit.entity.Villager villager) {
        this.villager = villager;
        this.currentWorld = villager.getWorld();
        this.nmsVillager = (CraftVillager) villager;
    }

    public void walkTo(Location target){
        nmsVillager.getHandle().getNavigation().moveTo(target.x(), target.y(), target.z(), 1);
    }

    public void walkToWorkStation(){
        nmsVillager.getHandle().getNavigation().moveTo(locWork.x(), locWork.y(), locWork.z(), 1);
    }

    public void setWorkingStation(Block block){
        this.workingStation = block;
        this.locWork = block.getLocation();
    }

    public Profession getProfession(){
        return profession;
    }

    public Location getWorkstationLocation(){
        String stored = villager.getPersistentDataContainer().get(
                WORKING_TABLE_KEY,
                PersistentDataType.STRING
        );

        if(stored != null) {
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

    static enum Profession {

        BEEKEEPER("beekeeper");

        private final NamespacedKey key;

        Profession(String id) {
            this.key = new NamespacedKey(ItemForge.getForge(), id);
        }

        public NamespacedKey getKey() {
            return key;
        }
    }
}



