package de.relaxogames.snorlaxItemForge.teams;

import net.kyori.adventure.text.format.NamedTextColor;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeamModel {
    private String name;
    private UUID leader;
    private Set<UUID> members;
    private String prefix;
    private String colorName;
    private Set<UUID> requests;

    public TeamModel(String name, UUID leader) {
        this.name = name;
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
        this.prefix = "";
        this.colorName = NamedTextColor.WHITE.toString();
        this.requests = new HashSet<>();
    }

    // Constructor for deserialization
    public TeamModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void setMembers(Set<UUID> members) {
        this.members = members;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public NamedTextColor getColor() {
        NamedTextColor color = NamedTextColor.NAMES.value(colorName);
        return color != null ? color : NamedTextColor.WHITE;
    }

    public void setColor(NamedTextColor color) {
        this.colorName = color.toString();
    }

    public Set<UUID> getRequests() {
        return requests;
    }

    public void setRequests(Set<UUID> requests) {
        this.requests = requests;
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public void addRequest(UUID uuid) {
        requests.add(uuid);
    }

    public void removeRequest(UUID uuid) {
        requests.remove(uuid);
    }
}
