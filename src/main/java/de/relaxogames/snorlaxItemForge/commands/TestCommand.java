package de.relaxogames.snorlaxItemForge.commands;

import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        Player p = (Player) sender;

        for (Entity entity : p.getNearbyEntities(40, 40, 40)) {
            if (!(entity instanceof Villager villager)) continue; // nur Villager berücksichtigen
            CustomVillager villager1 = VillagerWrapper.from(villager);
            if (villager1 == null)continue;
            villager1.work();
        }

        return false;
    }
}
