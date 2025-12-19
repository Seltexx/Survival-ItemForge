package de.relaxogames.snorlaxItemForge.listener.villager;

import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import de.relaxogames.snorlaxItemForge.util.villager.CustomVillager;
import de.relaxogames.snorlaxItemForge.util.villager.VillagerWrapper;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FirecrackerListener implements Listener {

    @EventHandler
    public void onTrade(PlayerTradeEvent e){
        Player trader = e.getPlayer();
        Villager villager = (Villager) e.getVillager();

        if (villager == null || villager.isDead())return;
        CustomVillager customVillager = VillagerWrapper.load(villager);
        if (customVillager == null)return;
        if (customVillager.getProfession() == null)return;
        if (!customVillager.getProfession().equals(CustomVillager.Profession.FIRECRACKER))return;
        Advancements.playout(trader, Advancement.FIRECRACKER_FIRST_TRADE);
        if (!e.getTrade().getResult().getType().isRecord())return;
        Advancements.playout(trader, Advancement.SONGS_ROOT);
    }

}
