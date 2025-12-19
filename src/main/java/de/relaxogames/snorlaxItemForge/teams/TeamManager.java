package de.relaxogames.snorlaxItemForge.teams;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.relaxogames.snorlaxItemForge.ItemForge;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class TeamManager {
    private static final Map<String, TeamModel> teams = new HashMap<>();
    private static final File file = new File(ItemForge.getForge().getDataFolder(), "teams.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void loadTeams() {
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, TeamModel>>(){}.getType();
            Map<String, TeamModel> loadedTeams = gson.fromJson(reader, type);
            if (loadedTeams != null) {
                teams.clear();
                teams.putAll(loadedTeams);
                for (TeamModel team : teams.values()) {
                    syncWithMinecraft(team);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveTeams() {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(teams, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TeamModel createTeam(String name, UUID leader) {
        if (teams.containsKey(name)) return null;
        TeamModel team = new TeamModel(name, leader);
        teams.put(name, team);
        syncWithMinecraft(team);
        saveTeams();
        return team;
    }

    public static void deleteTeam(String name) {
        TeamModel model = teams.remove(name);
        if (model != null) {
            Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = sb.getTeam(name);
            if (team != null) team.unregister();
            saveTeams();
        }
    }

    public static void syncWithMinecraft(TeamModel model) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = sb.getTeam(model.getName());
        if (team == null) {
            team = sb.registerNewTeam(model.getName());
        }
        team.prefix(Component.text(model.getPrefix()));
        team.color(model.getColor());

        // We should clear and re-add to be sure, but entries are names
        for (String entry : new HashSet<>(team.getEntries())) {
            team.removeEntry(entry);
        }

        for (UUID uuid : model.getMembers()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            String entry = op.getName();
            if (entry == null) {
                entry = uuid.toString();
            }
            team.addEntry(entry);
        }
    }

    public static TeamModel getPlayerTeam(UUID uuid) {
        for (TeamModel team : teams.values()) {
            if (team.getMembers().contains(uuid)) return team;
        }
        return null;
    }

    public static Collection<TeamModel> getAllTeams() {
        return teams.values();
    }

    public static TeamModel getTeam(String name) {
        return teams.get(name);
    }
}
