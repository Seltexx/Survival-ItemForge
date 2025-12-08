package de.relaxogames.snorlaxItemForge.advancement;

public enum Advancement {

    GLOBAL_ALCHEMIST("advancement_global_alchemist");

    String fileName;

    Advancement(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
