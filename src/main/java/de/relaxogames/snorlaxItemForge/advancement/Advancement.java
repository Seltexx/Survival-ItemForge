package de.relaxogames.snorlaxItemForge.advancement;

public enum Advancement {

    GLOBAL_ALCHEMIST("advancement_is_this_vape_liquid", "brewed_tincture"),
    GLOBAL_ALCHEMIST_EXPLODED("advancement_tincture_explosion", "tincture_explosion");

    String fileName;
    String trigger;
    Advancement(String fileName, String trigger) {
        this.fileName = fileName;
        this.trigger = trigger;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getFileName() {
        return fileName;
    }
}
