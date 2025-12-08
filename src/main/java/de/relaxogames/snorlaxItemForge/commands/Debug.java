package de.relaxogames.snorlaxItemForge.commands;

import de.relaxogames.languages.ServerColors;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Debug implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player p = (Player) sender;
        ItemBuilder ib = new ItemBuilder(Component.text(
                "Test-Item"
        ).color(ServerColors.Chartreuse2.color()), Material.GHAST_TEAR, new ArrayList<>(), new NamespacedKey(ItemForge.getForge(), "brew-test"), 12);
        ib.addCustomModelData(12);
        p.getInventory().addItem(ib.getItem());

        return false;
    }
}
