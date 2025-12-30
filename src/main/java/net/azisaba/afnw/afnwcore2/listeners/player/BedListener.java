package net.azisaba.afnw.afnwcore2.listeners.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BedListener implements Listener {
    private final Map<UUID, Integer> clicks = new HashMap<>();
    private final Plugin plugin;

    public BedListener(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock().getType().name().contains("_BED")) {
            int current = clicks.getOrDefault(e.getPlayer().getUniqueId(), 0);
            if (current < 2) {
                e.getPlayer().sendMessage(Component.text("あと" + (2 - current) + "回クリックしてください...", NamedTextColor.YELLOW));
                e.setCancelled(true);
                clicks.put(e.getPlayer().getUniqueId(), current + 1);
                Bukkit.getScheduler().runTaskLater(plugin, () -> clicks.remove(e.getPlayer().getUniqueId()), 20 * 3);
            } else {
                clicks.remove(e.getPlayer().getUniqueId());
            }
        }
    }
}
