package de.relaxogames.snorlaxItemForge.listener.villager.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomVillagerWorkTickEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private long time;
    private World world;

    public CustomVillagerWorkTickEvent(World world, long time) {
        this.time = time;
        this.world = world;
    }

    public long getTime() {
        return time;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
