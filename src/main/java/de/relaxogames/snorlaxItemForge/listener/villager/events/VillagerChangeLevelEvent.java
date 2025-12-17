package de.relaxogames.snorlaxItemForge.listener.villager.events;

import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VillagerChangeLevelEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final CustomVillager.Level oldLevel;
    private final CustomVillager.Level newLevel;
    private final CustomVillager villager;

    public VillagerChangeLevelEvent(CustomVillager.Level newLevel, CustomVillager.Level oldLevel, CustomVillager villager) {
        this.newLevel = newLevel;
        this.oldLevel = oldLevel;
        this.villager = villager;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public CustomVillager.Level getNewLevel() {
        return newLevel;
    }

    public CustomVillager.Level getOldLevel() {
        return oldLevel;
    }

    public CustomVillager getVillager() {
        return villager;
    }
}
