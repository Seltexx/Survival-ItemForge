package de.relaxogames.snorlaxItemForge.listener.villager.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomVillagerWorkTickEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final long time;
    private final World world;

    public CustomVillagerWorkTickEvent(World world, long time) {
        this.world = world;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
