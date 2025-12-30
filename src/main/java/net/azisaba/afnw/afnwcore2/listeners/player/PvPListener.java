package net.azisaba.afnw.afnwcore2.listeners.player;

import net.azisaba.afnw.afnwcore2.AfnwCore2;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public record PvPListener(AfnwCore2 plugin) implements Listener {
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (e.getDamager() instanceof Player killer) {
            if (!plugin.pvpEnabled.contains(killer.getUniqueId()) || !plugin.pvpEnabled.contains(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }
        if (e.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
            if (shooter != e.getEntity() && !plugin.pvpEnabled.contains(shooter.getUniqueId()) || !plugin.pvpEnabled.contains(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }
}
