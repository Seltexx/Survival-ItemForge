package de.relaxogames.snorlaxItemForge.listener.villager.events;

import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerEngageBeeOfBeekeeperEvent extends Event implements Cancellable {


    boolean canceled = false;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    Player damager;
    Entity hitBee;
    List<CustomVillager> angryVillager;

    public PlayerEngageBeeOfBeekeeperEvent(Player damager, Entity hitBee, List<CustomVillager> angryVillager) {
        this.angryVillager = angryVillager;
        this.damager = damager;
        this.hitBee = hitBee;
    }

    public List<CustomVillager> getAngryVillager() {
        return angryVillager;
    }

    public Player getDamager() {
        return damager;
    }

    public Entity getHitBee() {
        return hitBee;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }
}
