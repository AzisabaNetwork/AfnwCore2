package net.azisaba.afnw.afnwcore2.listeners.player;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Optional;

public class FishingListener implements Listener {
    @EventHandler
    public void on(PlayerFishEvent e) {
        if (e.getCaught() != null) {
            double baseChance = 0.01; // 1%
            double playerLuck = Optional.ofNullable(e.getPlayer().getAttribute(Attribute.LUCK)).map(AttributeInstance::getValue).orElse(0.0);
            double chance = baseChance + playerLuck * 0.001; // 0.1% per luck level
            // calculate chance
            if (Math.random() < chance) {
                // drop shard
                e.getPlayer().getInventory().addItem(MythicBukkit.inst().getItemManager().getItemStack("Rare_Lootbox_Shard"));
            }
        }
    }
}
