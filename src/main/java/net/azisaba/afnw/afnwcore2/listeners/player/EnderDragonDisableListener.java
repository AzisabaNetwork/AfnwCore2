package net.azisaba.afnw.afnwcore2.listeners.player;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnderDragonDisableListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getItem() == null) return;
        Material inHand = e.getItem().getType();
        if ((inHand == Material.ENDER_DRAGON_SPAWN_EGG || inHand == Material.WITHER_SPAWN_EGG)
                && !e.getPlayer().getWorld().getName().equals("afnw_the_end")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        if (e.getEntity().getType() == EntityType.ENDER_DRAGON && !e.getEntity().getWorld().getName().equals("afnw_the_end")) {
            e.setCancelled(true);
        }
    }
}
