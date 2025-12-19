package de.relaxogames.snorlaxItemForge.teams;

import de.relaxogames.snorlaxItemForge.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamGUI {

    public static final Component MAIN_TITLE = Component.text("Team Menu");
    public static final Component BROWSE_TITLE = Component.text("Browse Teams");
    public static final Component MY_TEAM_TITLE = Component.text("My Team");
    public static final Component SETTINGS_TITLE = Component.text("Team Settings");
    public static final Component REQUESTS_TITLE = Component.text("Join Requests");
    public static final Component COLOR_TITLE = Component.text("Select Color");

    public static void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, MAIN_TITLE);
        TeamModel team = TeamManager.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            inv.setItem(11, new ItemBuilder(Component.text("Create Team", NamedTextColor.GREEN), Material.NETHER_STAR, List.of(Component.text("Click to create a new team"))).getItem());
        } else {
            inv.setItem(11, new ItemBuilder(Component.text("My Team", NamedTextColor.GOLD), Material.WHITE_BANNER, List.of(Component.text("Manage your team: " + team.getName()))).getItem());
        }

        inv.setItem(15, new ItemBuilder(Component.text("Browse Teams", NamedTextColor.AQUA), Material.COMPASS, List.of(Component.text("View and join other teams"))).getItem());

        player.openInventory(inv);
    }

    public static void openBrowseTeams(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, BROWSE_TITLE);
        int slot = 0;
        for (TeamModel team : TeamManager.getAllTeams()) {
            if (slot >= 54) break;
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Leader: " + Bukkit.getOfflinePlayer(team.getLeader()).getName()));
            lore.add(Component.text("Members: " + team.getMembers().size()));
            lore.add(Component.text(""));
            if (team.getMembers().contains(player.getUniqueId())) {
                lore.add(Component.text("You are a member", NamedTextColor.GREEN));
            } else if (team.getRequests().contains(player.getUniqueId())) {
                lore.add(Component.text("Request pending...", NamedTextColor.YELLOW));
            } else {
                lore.add(Component.text("Click to request to join", NamedTextColor.GOLD));
            }

            inv.setItem(slot++, new ItemBuilder(Component.text(team.getName(), team.getColor()), Material.PAPER, lore).getItem());
        }
        player.openInventory(inv);
    }

    public static void openMyTeamMenu(Player player) {
        TeamModel team = TeamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) {
            openMainMenu(player);
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 27, MY_TEAM_TITLE);
        boolean isLeader = team.getLeader().equals(player.getUniqueId());

        inv.setItem(10, new ItemBuilder(Component.text("Members", NamedTextColor.BLUE), Material.PLAYER_HEAD, List.of(Component.text("View all team members"))).getItem());

        if (isLeader) {
            inv.setItem(12, new ItemBuilder(Component.text("Settings", NamedTextColor.GRAY), Material.COMPARATOR, List.of(Component.text("Change prefix and color"))).getItem());
            inv.setItem(14, new ItemBuilder(Component.text("Requests", NamedTextColor.YELLOW), Material.WRITABLE_BOOK, List.of(Component.text("Manage join requests"))).getItem());
            inv.setItem(16, new ItemBuilder(Component.text("Disband Team", NamedTextColor.RED), Material.BARRIER, List.of(Component.text("Permanently delete the team"))).getItem());
        } else {
            inv.setItem(16, new ItemBuilder(Component.text("Leave Team", NamedTextColor.RED), Material.BARRIER, List.of(Component.text("Leave this team"))).getItem());
        }

        player.openInventory(inv);
    }

    public static void openSettingsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, SETTINGS_TITLE);
        inv.setItem(11, new ItemBuilder(Component.text("Change Prefix", NamedTextColor.YELLOW), Material.NAME_TAG, List.of(Component.text("Set your team's prefix"))).getItem());
        inv.setItem(15, new ItemBuilder(Component.text("Change Color", NamedTextColor.AQUA), Material.CYAN_DYE, List.of(Component.text("Set your team's color"))).getItem());
        player.openInventory(inv);
    }

    public static void openColorMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 18, COLOR_TITLE);
        NamedTextColor[] colors = {
                NamedTextColor.WHITE, NamedTextColor.GRAY, NamedTextColor.DARK_GRAY, NamedTextColor.BLACK,
                NamedTextColor.RED, NamedTextColor.DARK_RED, NamedTextColor.GOLD, NamedTextColor.YELLOW,
                NamedTextColor.GREEN, NamedTextColor.DARK_GREEN, NamedTextColor.AQUA, NamedTextColor.DARK_AQUA,
                NamedTextColor.BLUE, NamedTextColor.DARK_BLUE, NamedTextColor.LIGHT_PURPLE, NamedTextColor.DARK_PURPLE
        };

        Material[] woolColors = {
                Material.WHITE_WOOL, Material.LIGHT_GRAY_WOOL, Material.GRAY_WOOL, Material.BLACK_WOOL,
                Material.RED_WOOL, Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL,
                Material.LIME_WOOL, Material.GREEN_WOOL, Material.CYAN_WOOL, Material.CYAN_WOOL,
                Material.LIGHT_BLUE_WOOL, Material.BLUE_WOOL, Material.MAGENTA_WOOL, Material.PURPLE_WOOL
        };

        for (int i = 0; i < colors.length; i++) {
            inv.setItem(i, new ItemBuilder(Component.text(colors[i].toString(), colors[i]), woolColors[i], List.of(Component.text("Select this color"))).getItem());
        }
        player.openInventory(inv);
    }

    public static void openRequestsMenu(Player player) {
        TeamModel team = TeamManager.getPlayerTeam(player.getUniqueId());
        if (team == null || !team.getLeader().equals(player.getUniqueId())) return;

        Inventory inv = Bukkit.createInventory(null, 54, REQUESTS_TITLE);
        int slot = 0;
        for (UUID requester : team.getRequests()) {
            if (slot >= 54) break;
            OfflinePlayer op = Bukkit.getOfflinePlayer(requester);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta == null) continue;
            meta.setOwningPlayer(op);
            meta.displayName(Component.text(op.getName() != null ? op.getName() : requester.toString(), NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("Left-Click to Accept"), Component.text("Right-Click to Deny")));
            skull.setItemMeta(meta);

            inv.setItem(slot++, skull);
        }
        player.openInventory(inv);
    }

    public static void openMembersMenu(Player player) {
        TeamModel team = TeamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) return;

        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Team Members: " + team.getName()));
        int slot = 0;
        for (UUID member : team.getMembers()) {
            if (slot >= 54) break;
            OfflinePlayer op = Bukkit.getOfflinePlayer(member);
            String role = member.equals(team.getLeader()) ? "Leader" : "Member";

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(op);
                meta.displayName(Component.text(op.getName() != null ? op.getName() : member.toString(), NamedTextColor.GREEN));
                meta.lore(List.of(Component.text("Role: " + role)));
                skull.setItemMeta(meta);
            }

            inv.setItem(slot++, skull);
        }
        player.openInventory(inv);
    }
}
