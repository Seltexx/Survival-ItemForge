package de.relaxogames.snorlaxItemForge.advancement;

public enum Advancement {
    SNORLAX_ROOT("advancement_snorlaxlabs", "tick"),
    BREWED_TINCTURE("advancement_thats_not_what_its_look_like", "brewed_tincture"),
    GLOBAL_ALCHEMIST_EXPLODED("advancement_tincture_explosion", "tincture_explosion"),
    PEBBLED_SNOWBALL("advancement_pebbled_snowball_hit", "pebbled_snowball_hit"),
    BEEKEEPER_FIRSTSTRADE("advancement_beekeeper_first_trade", "trade_beekeeper"),
    PILOT_BEE("advancement_beekeeper_pilotbee", "happy_ghast_honey"),
    PEBBLED_SNOWBALL_DEATH("advancement_pebbled_snowball_death", "pebbled_snowball_death"),

    LISTENED_TO_YMCA("advancement_disc_ymca", "listend_YMCA"),
    LISTENED_TO_RICKROLL("advancement_disc_rickroll", "listened_rickroll"),
    LISTENED_TO_GREEK_WINE("advancement_disc_rickroll", "listened_rickroll"),
    LISTENED_TO_GRAVITY_FALLS_THEME("advancement_disc_rickroll", "listened_rickroll"),
    LISTENED_TO_BEETHOVENS_NO5("advancement_disc_rickroll", "listened_rickroll"),
    LISTENED_TO_LET_IT_SNOW("advancement_disc_rickroll", "listened_rickroll");

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
