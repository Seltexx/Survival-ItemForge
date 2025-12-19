package de.relaxogames.snorlaxItemForge.teams;

import de.relaxogames.snorlaxItemForge.ItemForge;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamListener implements Listener {

    private enum InputType {
        TEAM_NAME, PREFIX
    }

    private static final Map<UUID, InputType> awaitingInput = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Component title = event.getView().title();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        String plainTitle = PlainTextComponentSerializer.plainText().serialize(title);

        if (plainTitle.equals(PlainTextComponentSerializer.plainText().serialize(TeamGUI.MAIN_TITLE))) {
            event.setCancelled(true);
            if (clicked.getType() == Material.NETHER_STAR) {
                player.closeInventory();
                player.sendMessage(Component.text("Please type your team name in chat:", NamedTextColor.YELLOW));
                awaitingInput.put(player.getUniqueId(), InputType.TEAM_NAME);
            } else if (clicked.getType() == Material.WHITE_BANNER) {
                TeamGUI.openMyTeamMenu(player);
            } else if (clicked.getType() == Material.COMPASS) {
                TeamGUI.openBrowseTeams(player);
            }
        } else if (plainTitle.equals(PlainTextComponentSerializer.plainText().serialize(TeamGUI.BROWSE_TITLE))) {
            event.setCancelled(true);
            String teamName = PlainTextComponentSerializer.plainText().serialize(clicked.getItemMeta().itemName());
            TeamModel team = TeamManager.getTeam(teamName);
            if (team != null) {
                if (team.getMembers().contains(player.getUniqueId())) {
                    player.sendMessage(Component.text("You are already in this team!", NamedTextColor.RED));
                } else if (team.getRequests().contains(player.getUniqueId())) {
                    player.sendMessage(Component.text("You have already requested to join this team!", NamedTextColor.RED));
                } else {
                    team.addRequest(player.getUniqueId());
                    TeamManager.saveTeams();
                    player.sendMessage(Component.text("Request sent to " + team.getName(), NamedTextColor.GREEN));
                    TeamGUI.openBrowseTeams(player);
                }
            }
        } else if (plainTitle.equals(PlainTextComponentSerializer.plainText().serialize(TeamGUI.MY_TEAM_TITLE))) {
            event.setCancelled(true);
            if (clicked.getType() == Material.COMPARATOR) {
                TeamGUI.openSettingsMenu(player);
            } else if (clicked.getType() == Material.WRITABLE_BOOK) {
                TeamGUI.openRequestsMenu(player);
            } else if (clicked.getType() == Material.BARRIER) {
                TeamModel team = TeamManager.getPlayerTeam(player.getUniqueId());
                if (team != null) {
                    if (team.getLeader().equals(player.getUniqueId())) {
                        TeamManager.deleteTeam(team.getName());
                        player.sendMessage(Component.text("Team disbanded.", NamedTextColor.RED));
                    } else {
                        team.removeMember(player.getUniqueId());
                        TeamManager.syncWithMinecraft(team);
                        TeamManager.saveTeams();
                        player.sendMessage(Component.text("You left the team.", NamedTextColor.RED));
                    }
                    player.closeInventory();
                }
            } else if (clicked.getType() == Material.PLAYER_HEAD) {
                TeamGUI.openMembersMenu(player);
            }
        } else if (plainTitle.startsWith("Team Members:")) {
            event.setCancelled(true);
        } else if (plainTitle.equals(PlainTextComponentSerializer.plainText().serialize(TeamGUI.SETTINGS_TITLE))) {
            event.setCancelled(true);
            if (clicked.getType() == Material.NAME_TAG) {
                player.closeInventory();
                player.sendMessage(Component.text("Please type your new prefix in chat:", NamedTextColor.YELLOW));
                awaitingInput.put(player.getUniqueId(), InputType.PREFIX);
            } else if (clicked.getType() == Material.CYAN_DYE) {
                TeamGUI.openColorMenu(player);
            }
        } else if (plainTitle.equals(PlainTextComponentSerializer.plainText().serialize(TeamGUI.COLOR_TITLE))) {
            event.setCancelled(true);
            String colorName = PlainTextComponentSerializer.plainText().serialize(clicked.getItemMeta().itemName());
            NamedTextColor color = NamedTextColor.NAMES.value(colorName.toLowerCase());
            if (color != null) {
                TeamModel team = TeamManager.getPlayerTeam(player.getUniqueId());
                if (team != null && team.getLeader().equals(player.getUniqueId())) {
                    team.setColor(color);
                    TeamManager.syncWithMinecraft(team);
                    TeamManager.saveTeams();
                    player.sendMessage(Component.text("Team color updated!", NamedTextColor.GREEN));
                    TeamGUI.openSettingsMenu(player);
                }
            }
        } else if (plainTitle.equals(PlainTextComponentSerializer.plainText().serialize(TeamGUI.REQUESTS_TITLE))) {
            event.setCancelled(true);
            Component displayName = clicked.getItemMeta().displayName();
            if (displayName == null) return;

            String playerName = PlainTextComponentSerializer.plainText().serialize(displayName);
            TeamModel team = TeamManager.getPlayerTeam(player.getUniqueId());
            if (team != null && team.getLeader().equals(player.getUniqueId())) {
                UUID requesterUUID = null;
                for (UUID uuid : team.getRequests()) {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                    if (playerName.equalsIgnoreCase(op.getName()) || playerName.equals(uuid.toString())) {
                        requesterUUID = uuid;
                        break;
                    }
                }

                if (requesterUUID != null) {
                    if (event.isLeftClick()) {
                        team.removeRequest(requesterUUID);
                        team.addMember(requesterUUID);
                        TeamManager.syncWithMinecraft(team);
                        TeamManager.saveTeams();
                        player.sendMessage(Component.text("Accepted " + playerName, NamedTextColor.GREEN));
                        Player requester = Bukkit.getPlayer(requesterUUID);
                        if (requester != null) requester.sendMessage(Component.text("You joined " + team.getName(), NamedTextColor.GREEN));
                    } else if (event.isRightClick()) {
                        team.removeRequest(requesterUUID);
                        TeamManager.saveTeams();
                        player.sendMessage(Component.text("Denied " + playerName, NamedTextColor.RED));
                    }
                    TeamGUI.openRequestsMenu(player);
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (!awaitingInput.containsKey(player.getUniqueId())) return;

        event.setCancelled(true);
        InputType type = awaitingInput.remove(player.getUniqueId());
        String input = PlainTextComponentSerializer.plainText().serialize(event.originalMessage()).trim();

        Bukkit.getScheduler().runTask(ItemForge.getForge(), () -> {
            if (type == InputType.TEAM_NAME) {
                if (input.length() < 3 || input.length() > 16) {
                    player.sendMessage(Component.text("Team name must be 3-16 characters!", NamedTextColor.RED));
                    return;
                }
                if (TeamManager.getTeam(input) != null) {
                    player.sendMessage(Component.text("Team name already taken!", NamedTextColor.RED));
                    return;
                }
                if (TeamManager.getPlayerTeam(player.getUniqueId()) != null) {
                    player.sendMessage(Component.text("You are already in a team!", NamedTextColor.RED));
                    return;
                }
                TeamManager.createTeam(input, player.getUniqueId());
                player.sendMessage(Component.text("Team '" + input + "' created!", NamedTextColor.GREEN));
            } else if (type == InputType.PREFIX) {
                TeamModel team = TeamManager.getPlayerTeam(player.getUniqueId());
                if (team != null && team.getLeader().equals(player.getUniqueId())) {
                    if (input.length() > 10) {
                        player.sendMessage(Component.text("Prefix too long! (Max 10)", NamedTextColor.RED));
                        return;
                    }
                    team.setPrefix(input + " | ");
                    TeamManager.syncWithMinecraft(team);
                    TeamManager.saveTeams();
                    player.sendMessage(Component.text("Prefix updated to '" + input + "'", NamedTextColor.GREEN));
                }
            }
        });
    }
}
