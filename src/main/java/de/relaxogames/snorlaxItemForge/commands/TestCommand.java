package de.relaxogames.snorlaxItemForge.commands;

import de.relaxogames.api.Lingo;
import de.relaxogames.languages.Locale;
import de.relaxogames.languages.ServerColors;
import de.relaxogames.snorlaxItemForge.listener.musicdiscs.MusicDiscs;
import de.relaxogames.snorlaxItemForge.util.ItemBuilder;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        Player p = (Player) sender;
        if (args.length == 0) {
            ItemBuilder honeyBottle = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Name"))
                    .color(NamedTextColor.GOLD), Material.HONEY_BOTTLE, List.of(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Worst"))));

            ItemStack honey = honeyBottle.getItem();
            ItemMeta meta = honey.getItemMeta();
            meta.setCustomModelData(111);
            meta.setMaxStackSize(16);
            honey.setItemMeta(meta);

            ItemBuilder h2 = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Name"))
                    .color(NamedTextColor.GOLD), Material.HONEY_BOTTLE, List.of(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Bad"))));

            ItemStack hm2 = h2.getItem();
            ItemMeta hmm2 = hm2.getItemMeta();
            hmm2.setCustomModelData(222);
            hmm2.setMaxStackSize(16);
            hm2.setItemMeta(hmm2);

            ItemBuilder h3 = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Name"))
                    .color(NamedTextColor.GOLD), Material.HONEY_BOTTLE, List.of(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Okay"))));

            ItemStack hm3 = h3.getItem();
            ItemMeta hmm3 = hm3.getItemMeta();
            hmm3.setCustomModelData(333);
            hmm3.setMaxStackSize(16);
            hm3.setItemMeta(hmm3);


            ItemBuilder h4 = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Name"))
                    .color(NamedTextColor.GOLD), Material.HONEY_BOTTLE, List.of(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Beeworker-Honey-Best"))));

            ItemStack hm4 = h4.getItem();
            ItemMeta hmm4 = hm4.getItemMeta();
            hmm4.setCustomModelData(444);
            hmm4.setMaxStackSize(16);
            hm4.setItemMeta(hmm4);

            p.getInventory().addItem(honey, hm2, hm3, hm4);

            p.getInventory().addItem(
                    MusicDiscs.YMCA.createItem(),
                    MusicDiscs.BEETHOVENS_NO5.createItem(),
                    MusicDiscs.GRAVITY_FALL_THEME.createItem(),
                    MusicDiscs.GRIECHISCHER_WEIN.createItem(),
                    MusicDiscs.LET_IT_SNOW.createItem(),
                    MusicDiscs.RICK_ROLL.createItem()
            );

            List<Component> ammoLore = new ArrayList<>();
            ammoLore.add(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Head-Drop-Ammunition-Lore")));
            ItemBuilder ammonation = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Head-Drop-Ammunition-Name"))
                    .color(ServerColors.Red3.color()), Material.FIREWORK_ROCKET, ammoLore);

            ItemStack ammoFirework = ammonation.getItem();
            ItemMeta ammoMeta = ammoFirework.getItemMeta();
            ammoMeta.setCustomModelData(2006);
            ammoFirework.setItemMeta(ammoMeta);
            ammoFirework.setAmount(64);

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Head-Drop-Book-Lore")));
            ItemBuilder enchantedBook = new ItemBuilder(Component.text(Lingo.getLibrary().getMessage(Locale.GERMAN, "Head-Drop-Book-Name"))
                    .color(NamedTextColor.WHITE), Material.ENCHANTED_BOOK, lore);

            ItemStack executionerBook = enchantedBook.getItem();
            ItemMeta testMeta = executionerBook.getItemMeta();
            testMeta.setCustomModelData(80);
            executionerBook.setItemMeta(testMeta);

            p.getInventory().addItem(ammoFirework, executionerBook);
        }
        return false;
    }
}
