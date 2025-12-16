package de.relaxogames.snorlaxItemForge.util.villager;

import com.destroystokyo.paper.ParticleBuilder;
import com.jeff_media.customblockdata.CustomBlockData;
import de.relaxogames.snorlaxItemForge.FileManager;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomVillager {

    protected final FileManager fM = new FileManager();
    protected final Villager villager;
    protected Merchant merchant;
    protected Level level;
    protected int experience = 0;
    protected List<MerchantRecipe> stockList;
    protected final CraftVillager nmsVillager;
    protected final World currentWorld;

    protected Block workingStation;
    protected Location locWork;
    protected Profession profession;

    protected static final NamespacedKey WORKING_TABLE_KEY =
            new NamespacedKey(ItemForge.getForge(), "working_station");
    protected static final NamespacedKey PROFESSION_KEY =
            new NamespacedKey(ItemForge.getForge(), "villager_profession");
    protected static final NamespacedKey PROFESSION_LEVEL =
            new NamespacedKey(ItemForge.getForge(), "villager_profession_level");
    protected static final NamespacedKey BLOCK_BLOCKED_BY =
            new NamespacedKey(ItemForge.getForge(), "villager_uuid");
    private static final NamespacedKey TRADE_LOCKER =
            new NamespacedKey(ItemForge.getForge(), "trade_locker");

    private static final NamespacedKey KEY_TRADE_RESULT =
            new NamespacedKey(ItemForge.getForge(), "trade_result");

    private static final NamespacedKey KEY_INGREDIENT_ITEM =
            new NamespacedKey(ItemForge.getForge(), "ingredient_item");

    private static final NamespacedKey KEY_INGREDIENTS =
            new NamespacedKey(ItemForge.getForge(), "ingredients");

    private static final NamespacedKey KEY_USES =
            new NamespacedKey(ItemForge.getForge(), "uses");

    private static final NamespacedKey KEY_MAX_USES =
            new NamespacedKey(ItemForge.getForge(), "max_uses");

    private static final NamespacedKey KEY_DEMAND =
            new NamespacedKey(ItemForge.getForge(), "demand");

    private static final NamespacedKey KEY_PRICE_MULT =
            new NamespacedKey(ItemForge.getForge(), "price_multiplier");

    public CustomVillager(Villager villager) {
        this.villager = villager;
        this.currentWorld = villager.getWorld();
        this.nmsVillager = (CraftVillager) villager;
        loadPersistent();
        moveToNearestWorkingStation();
    }

    public CustomVillager(Villager villager, Component invTitle) {
        this.villager = villager;
        this.merchant = villager;
        this.currentWorld = villager.getWorld();
        this.nmsVillager = (CraftVillager) villager;
        loadPersistent();
        moveToNearestWorkingStation();
        villager.customName(invTitle);
        villager.setCustomNameVisible(false);
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
        getLevel();
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

                if (villager.getLocation().distanceSquared(target) <= 2) {
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
        level = Level.NOVICE;
        experience = 0;

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
                    removeWorkingStation();
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
    protected abstract void initializeTrades();
    public abstract void replenishTrades();
    public abstract List<MerchantRecipe> buildMerchant();

    public List<MerchantRecipe> getStockList() {
        return stockList;
    }

    public void addExperience(int amount) {
        if (amount <= 0) return;
        if (level == null) level = getLevel();
        if (level == Level.MASTER) return;

        experience += amount;

        // Level-Up Loop
        while (experience >= level.getNeededExperience() && level != Level.MASTER) {
            experience -= level.getNeededExperience();
            Level next = Level.nextLevel(level);
            if (next == null) {
                experience = 0; // MASTER erreicht, überschüssige XP verfallen
                break;
            }
            level = next;
        }

        update();
    }

    public void update() {
        villager.setVillagerExperience(experience);
        villager.setVillagerLevel(level.getNmsLevel());

        villager.getPersistentDataContainer().set(
                PROFESSION_LEVEL,
                PersistentDataType.INTEGER,
                level.getNmsLevel()
        );
    }

    public void restock(){
        stockList = importTrades();
        villager.setRecipes(stockList);
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public Level getLevel() {
        if (level == null) {
            Integer fromPDC = villager.getPersistentDataContainer()
                    .get(PROFESSION_LEVEL, PersistentDataType.INTEGER);

            if (fromPDC == null) {
                level = Level.NOVICE;
            } else {
                level = Level.convertFromNMS(fromPDC);
            }
        }
        return level;
    }

    public void exportTrades(List<MerchantRecipe> trades) {
        PersistentDataContainer root = villager.getPersistentDataContainer();

        PersistentDataContainer[] tradeTags = trades.stream().map(trade -> {
            PersistentDataContainer tag =
                    root.getAdapterContext().newPersistentDataContainer();

            // Result
            tag.set(KEY_TRADE_RESULT,
                    PersistentDataType.BYTE_ARRAY,
                    ItemBuilder.serializeItem(trade.getResult()));

            // Meta
            tag.set(KEY_USES, PersistentDataType.INTEGER, trade.getUses());
            tag.set(KEY_MAX_USES, PersistentDataType.INTEGER, trade.getMaxUses());
            tag.set(KEY_DEMAND, PersistentDataType.INTEGER, trade.getDemand());
            tag.set(KEY_PRICE_MULT, PersistentDataType.FLOAT, trade.getPriceMultiplier());

            // Ingredients (max 2, Vanilla-Regel)
            PersistentDataContainer[] ingredientTags =
                    trade.getIngredients().stream()
                            .limit(2)
                            .map(ingredient -> {
                                PersistentDataContainer ingTag =
                                        root.getAdapterContext().newPersistentDataContainer();

                                ingTag.set(KEY_INGREDIENT_ITEM,
                                        PersistentDataType.BYTE_ARRAY,
                                        ItemBuilder.serializeItem(ingredient));

                                return ingTag;
                            }).toArray(PersistentDataContainer[]::new);

            tag.set(KEY_INGREDIENTS,
                    PersistentDataType.TAG_CONTAINER_ARRAY,
                    ingredientTags);

            return tag;
        }).toArray(PersistentDataContainer[]::new);

        root.set(TRADE_LOCKER,
                PersistentDataType.TAG_CONTAINER_ARRAY,
                tradeTags);
    }

    public List<MerchantRecipe> importTrades() {
        PersistentDataContainer root = villager.getPersistentDataContainer();

        PersistentDataContainer[] tradeTags =
                root.get(TRADE_LOCKER, PersistentDataType.TAG_CONTAINER_ARRAY);

        if (tradeTags == null || tradeTags.length == 0) {
            return List.of();
        }

        List<MerchantRecipe> trades = new ArrayList<>();

        for (PersistentDataContainer tag : tradeTags) {

            byte[] resultData =
                    tag.get(KEY_TRADE_RESULT, PersistentDataType.BYTE_ARRAY);

            if (resultData == null) continue;

            ItemStack result = ItemBuilder.deserializeItem(resultData);

            int maxUses = Math.max(1,
                    tag.getOrDefault(KEY_MAX_USES,
                            PersistentDataType.INTEGER, 1));

            int uses = Math.max(0,
                    tag.getOrDefault(KEY_USES,
                            PersistentDataType.INTEGER, 0));

            MerchantRecipe recipe = new MerchantRecipe(result, maxUses);
            recipe.setUses(uses);
            recipe.setDemand(tag.getOrDefault(KEY_DEMAND,
                    PersistentDataType.INTEGER, 0));
            recipe.setPriceMultiplier(tag.getOrDefault(KEY_PRICE_MULT,
                    PersistentDataType.FLOAT, 0f));

            PersistentDataContainer[] ingTags =
                    tag.get(KEY_INGREDIENTS, PersistentDataType.TAG_CONTAINER_ARRAY);

            // Ohne Ingredients → Trade überspringen (niemals ungültige Trades erzeugen!)
            if (ingTags == null || ingTags.length == 0) continue;

            for (int i = 0; i < Math.min(2, ingTags.length); i++) {
                byte[] ingData =
                        ingTags[i].get(KEY_INGREDIENT_ITEM,
                                PersistentDataType.BYTE_ARRAY);

                if (ingData != null) {
                    recipe.addIngredient(
                            ItemBuilder.deserializeItem(ingData));
                }
            }

            // Sicherheit: nachträglich prüfen
            if (recipe.getIngredients().isEmpty()) continue;

            trades.add(recipe);
        }

        return trades;
    }

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

    public void removeWorkingStation() {
        CustomBlockData blockData = new CustomBlockData(workingStation, ItemForge.getForge());
        blockData.remove(BLOCK_BLOCKED_BY);
        profession = null;
        locWork = null;
        workingStation = null;

        villager.getPersistentDataContainer().remove(WORKING_TABLE_KEY);
        villager.getPersistentDataContainer().remove(PROFESSION_KEY);
        villager.getPersistentDataContainer().remove(PROFESSION_LEVEL);
        villager.getPersistentDataContainer().remove(TRADE_LOCKER);
        villager.customName(null);
        villager.getEquipment().clear();
        villager.setProfession(Villager.Profession.NONE);
        villager.setVillagerType(Villager.Type.PLAINS);
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
    public enum Level{
        NOVICE("novice", 1, 100),
        APPRENTICE("apprentice", 2, 200),
        JOURNEYMAN("journeyman", 3, 300),
        EXPERT("expert", 4, 400),
        MASTER("master", 5, 500);

        String key;
        int nmsLevel;
        int neededExperience;
        Level(String key, int nmsLevel, int neededExperience) {
            this.key = key;
            this.nmsLevel = nmsLevel;
            this.neededExperience = neededExperience;
        }

        public int getNeededExperience() {
            return neededExperience;
        }

        public String getKeyString() {
            return key;
        }

        public NamespacedKey getKey() {
            return new NamespacedKey(ItemForge.getForge(), getKeyString());
        }

        public static Level nextLevel(Level level){
            if (level.equals(Level.MASTER))return null;
            for (Level next : Level.values()){
                if (next.getNmsLevel() <= level.getNmsLevel())continue;
                return next;
            }
            return MASTER;
        }

        public static Level convertFromNMS(int lvl){
            if (lvl <= 0)return NOVICE;
            for (Level enumLvl : Level.values()){
                if (enumLvl.getNmsLevel() != lvl)continue;
                return enumLvl;
            }
            return NOVICE;
        }

        public int getNmsLevel() {
            return nmsLevel;
        }
    }
}
