package de.relaxogames.snorlaxItemForge.listener.enchantments;

import de.relaxogames.api.Lingo;
import de.relaxogames.api.interfaces.LingoPlayer;
import de.relaxogames.api.interfaces.LingoUser;
import de.relaxogames.languages.Locale;
import de.relaxogames.languages.ServerColors;
import de.relaxogames.snorlaxItemForge.ItemForge;
import de.relaxogames.snorlaxItemForge.advancement.Advancement;
import de.relaxogames.snorlaxItemForge.advancement.Advancements;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class HeadshotEnchantmentListener implements Listener {

    private final NamespacedKey SHOTS_USED_CROSSBOW = new NamespacedKey(ItemForge.getForge(), "shots_fired");
    private final NamespacedKey PLAYER_KILLED = new NamespacedKey(ItemForge.getForge(), "player_killed");
    private final NamespacedKey HEAD_SHOT = new NamespacedKey(ItemForge.getForge(), "head_shot_shot");

    @EventHandler
    public void onEnchant(PrepareAnvilEvent e){
        Inventory anvilInv = e.getInventory();
        if (anvilInv == null || e.getView() == null)return;
        System.out.println("1");
        ItemStack crossbow = e.getInventory().getFirstItem();
        if (crossbow == null)return;
        if (!crossbow.getType().equals(Material.CROSSBOW))return;
        System.out.println("2");
        ItemStack book = e.getInventory().getSecondItem();
        if (book == null)return;
        System.out.println("3");
        if (!book.getType().equals(Material.ENCHANTED_BOOK))return;
        System.out.println("4");
        if (!book.hasItemMeta() || !book.getItemMeta().hasCustomModelData())return;
        System.out.println("5");
        if (book.getItemMeta().getCustomModelData() != 80)return;
        System.out.println("6");
        LingoUser enchanter = new LingoPlayer(e.getView().getPlayer().getUniqueId());

        ItemStack result = crossbow.clone();
        ItemMeta resultMeta = result.getItemMeta();
        if (result.getEnchantments().isEmpty())resultMeta.setEnchantmentGlintOverride(true);
        System.out.println("7");
        resultMeta.lore(updateLore(result, enchanter.getLanguage(), Enchantment.SWIFT_SNEAK));
        resultMeta.setCustomModelData(81);
        result.addUnsafeEnchantment(Enchantment.SWIFT_SNEAK, 1);
        result.addItemFlags(ItemFlag.HIDE_STORED_ENCHANTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        resultMeta.setRarity(ItemRarity.RARE);
        if (!e.getView().getRenameText().isEmpty())resultMeta.displayName(Component.text(e.getView().getRenameText()));
        System.out.println("8");
        result.setItemMeta(resultMeta);

        e.getView().setMaximumRepairCost(45);
        e.getView().setRepairCost(10);
        e.setResult(result);
    }

    @EventHandler
    public void onShot(EntityShootBowEvent ev){
        if (!ev.getBow().getType().equals(Material.CROSSBOW))return;
        ItemStack crossbow = ev.getBow();
        if (crossbow == null || crossbow.getItemMeta() == null || !crossbow.getItemMeta().hasCustomModelData())return;
        CrossbowMeta crossbowMeta = (CrossbowMeta) crossbow.getItemMeta();
        if (crossbowMeta.getCustomModelData() != 81)return;
        ItemStack knallerbse = ev.getConsumable();
        if (!knallerbse.hasItemMeta() || !knallerbse.getItemMeta().hasCustomModelData())return;
        if (knallerbse.getItemMeta().getCustomModelData() != 2006)return;

        Firework projectile = craftFirework(ev.getProjectile().getLocation());
        projectile.setShotAtAngle(true);
        projectile.getPersistentDataContainer().set(HEAD_SHOT, PersistentDataType.BOOLEAN, true);
        projectile.setVelocity(ev.getEntity().getEyeLocation().getDirection().multiply(1));
        projectile.setShooter(ev.getEntity());

        LingoUser lShooter = new LingoPlayer(ev.getEntity().getUniqueId());

        Long shots = crossbowMeta.getPersistentDataContainer().getOrDefault(SHOTS_USED_CROSSBOW, PersistentDataType.LONG, 1L);
        crossbowMeta.getPersistentDataContainer().set(SHOTS_USED_CROSSBOW, PersistentDataType.LONG, shots + 1);
        crossbowMeta.lore(updateLore(crossbow, lShooter.getLanguage() == null ? Locale.GERMAN : lShooter.getLanguage(), Enchantment.SWIFT_SNEAK));

        crossbow.setItemMeta(crossbowMeta);
        ev.getProjectile().remove();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player victim)) return;
        if (!(e.getDamager() instanceof Firework firework)) return;

        Boolean headshot = firework.getPersistentDataContainer()
                .get(HEAD_SHOT, PersistentDataType.BOOLEAN);

        System.out.println(headshot);

        if (!headshot) return;
        if (!(firework.getShooter() instanceof Player shooter)) return;
        double finalHealth = victim.getHealth() - e.getFinalDamage();
        if (finalHealth > 0) return;

        ItemStack crossbow = shooter.getInventory().getItemInMainHand();
        if (crossbow.getType() == Material.CROSSBOW && crossbow.hasItemMeta()) {
            ItemMeta meta = crossbow.getItemMeta();
            int kills = meta.getPersistentDataContainer()
                    .getOrDefault(PLAYER_KILLED, PersistentDataType.INTEGER, 0);
            meta.getPersistentDataContainer()
                    .set(PLAYER_KILLED, PersistentDataType.INTEGER, kills + 1);
            crossbow.setItemMeta(meta);
        }

        // 💀 Kopf droppen
        dropHead(victim);

        // 🏆 Advancement
        Advancements.playout(shooter, Advancement.HEAD_HUNTER);
    }

    private void dropHead(Player killed) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        meta.setOwningPlayer(killed);
        meta.displayName(Component.text(killed.getName() + "'s Kopf")
                .color(ServerColors.Red3.color())
                .decoration(TextDecoration.ITALIC, false));

        skull.setItemMeta(meta);
        killed.getWorld().dropItemNaturally(killed.getLocation(), skull);
    }


    private List<Component> updateLore(ItemStack stack, Locale locale, Enchantment banned){
        ItemMeta resultMet = stack.getItemMeta();
        List<Component> lore = new ArrayList<>();

        for (Enchantment enchantment : stack.getEnchantments().keySet()){
            if (enchantment.equals(banned))continue;
            lore.add(enchantment.displayName(stack.getEnchantmentLevel(enchantment)));
        }

        lore.add(Component.text(Lingo.getLibrary().getMessage(locale, "Head-Drop-Book-Lore")));
        lore.add(Component.text(" "));
        lore.add(Component.text(" "));

        Long shotsFromPDC = resultMet.getPersistentDataContainer().get(SHOTS_USED_CROSSBOW, PersistentDataType.LONG);
        Integer playerKilledFromPDC = resultMet.getPersistentDataContainer().get(PLAYER_KILLED, PersistentDataType.INTEGER);

        if (shotsFromPDC == null){
            shotsFromPDC = 0L;
            resultMet.getPersistentDataContainer().set(SHOTS_USED_CROSSBOW, PersistentDataType.INTEGER, 0);
        }
        if (playerKilledFromPDC == null){
            playerKilledFromPDC = 0;
            resultMet.getPersistentDataContainer().set(PLAYER_KILLED, PersistentDataType.INTEGER, 0);
        }

        for (int d = 1; d <= 2; d++) {
            lore.add(Component.text(Lingo.getLibrary().getMessage(locale, "Head-Drop-Crossbow-Lore-Enchant-" + d)
                    .replace("{MUNITION}", String.valueOf(shotsFromPDC))
                    .replace("{HEADS}", String.valueOf(playerKilledFromPDC))));
        }
        stack.setItemMeta(resultMet);
        return lore;
    }
    private Firework craftFirework(Location location){
        Firework projectile = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fwM = projectile.getFireworkMeta();
        Color redTone = Color.fromRGB(ServerColors.Red3.getR(), ServerColors.Red3.getG(), ServerColors.Red3.getB());
        fwM.addEffect(FireworkEffect.builder()
                .withColor(redTone)
                        .withColor(Color.BLACK)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withFlicker()
                .withTrail()
                .build());
        fwM.setPower(1);
        projectile.setFireworkMeta(fwM);
        return projectile;
    }
}
