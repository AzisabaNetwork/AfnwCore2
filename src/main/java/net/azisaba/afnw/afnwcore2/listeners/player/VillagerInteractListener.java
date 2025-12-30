package net.azisaba.afnw.afnwcore2.listeners.player;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * 製図家に対するクリックを無効化するクラス
 */
public record VillagerInteractListener() implements Listener {
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        onPlayerInteractEntity(e);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Villager villager) {
            if (villager.getProfession() == Villager.Profession.CARTOGRAPHER) {
                e.setCancelled(true);
            }
        }
    }
}
