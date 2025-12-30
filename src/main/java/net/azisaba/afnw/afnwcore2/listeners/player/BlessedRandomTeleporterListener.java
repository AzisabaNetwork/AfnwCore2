package net.azisaba.afnw.afnwcore2.listeners.player;

import net.azisaba.afnw.afnwcore2.util.Expr;
import net.azisaba.afnw.afnwcore2.util.item.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * Blessed Random Teleporterの実際の動作
 */
public record BlessedRandomTeleporterListener() implements Listener {
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        if (!"Blessed_Random_Teleporter".equals(ItemUtil.getMythicType(e.getItem()))) return;
        String worldName = e.getPlayer().getWorld().getName();
        if (!worldName.equals("afnw") && !worldName.equals("afnw_nether") && !worldName.equals("afnw_the_end")) {
            e.getPlayer().sendMessage(Component.text("このワールドでは使えません！", NamedTextColor.RED));
            e.setCancelled(true);
            return;
        }
        Location location;
        do {
            location = new Location(e.getPlayer().getWorld(), Expr.INSTANCE.randomInt(-100000, 100000) + 0.5, 100, Expr.INSTANCE.randomInt(-100000, 100000) + 0.5);
        } while (location.distance(e.getPlayer().getWorld().getSpawnLocation()) < 15000);
        location.clone().subtract(0, 1, 0).getBlock().setType(Material.HONEYCOMB_BLOCK);
        for (Entity entity : e.getPlayer().getNearbyEntities(2, 2, 2)) {
            if (entity instanceof Player) {
                entity.sendMessage(
                        Component.text(e.getPlayer().getName() + "の", NamedTextColor.YELLOW)
                                .append(Component.text("Blessed Random Teleporter", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                                .append(Component.text("の効果でテレポートされました！", NamedTextColor.YELLOW))
                );
                entity.teleport(location);
            }
        }
        e.getPlayer().teleport(location);
    }
}
