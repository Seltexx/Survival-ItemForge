package de.relaxogames.snorlaxItemForge.advancement;

public enum Advancement {
    SNORLAX_ROOT("advancement_snorlaxlabs", "tick"),
    BREWED_TINCTURE("advancement_thats_not_what_its_look_like", "brewed_tincture"),
    GLOBAL_ALCHEMIST_EXPLODED("advancement_tincture_explosion", "tincture_explosion"),
    PEBBLED_SNOWBALL("advancement_pebbled_snowball_hit", "pebbled_snowball_hit"),
    PEBBLED_SNOWBALL_DEATH("advancement_pebbled_snowball_death", "pebbled_snowball_death");

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
