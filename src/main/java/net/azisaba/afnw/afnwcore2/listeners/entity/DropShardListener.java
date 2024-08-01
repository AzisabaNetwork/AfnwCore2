package net.azisaba.afnw.afnwcore2.listeners.entity;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Optional;

public class DropShardListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) {
            return;
        }
        Player killer = e.getEntity().getKiller();
        double baseChance = 0.001; // 0.1%
        double playerLuck = Optional.ofNullable(killer.getAttribute(Attribute.GENERIC_LUCK)).map(AttributeInstance::getValue).orElse(0.0);
        double chance = baseChance + playerLuck * 0.0001; // 0.01% per luck level
        // calculate chance
        if (Math.random() < chance) {
            // drop shard
            killer.getInventory().addItem(MythicBukkit.inst().getItemManager().getItemStack("Rare_Lootbox_Shard"));
        }
    }
}
