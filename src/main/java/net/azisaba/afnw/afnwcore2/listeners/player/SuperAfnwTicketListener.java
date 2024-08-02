package net.azisaba.afnw.afnwcore2.listeners.player;

import net.azisaba.afnw.afnwcore2.commands.AfnwCommand;
import net.azisaba.afnw.afnwcore2.util.item.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Super Afnw Ticketの実際の動作
 */
public record SuperAfnwTicketListener(Plugin plugin) implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
        if (!"Super_Afnw_Ticket".equals(ItemUtil.getMythicType(e.getItem()))) return;
        e.getItem().setAmount(e.getItem().getAmount() - 1);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1000f, 1.5f);
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1000f, 1.75f), 3);
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1000f, 2.0f), 6);
            for (ItemStack value : player.getInventory().addItem(AfnwCommand.getRandomItem(50, 1)).values()) {
                ItemUtil.addToStashIfEnabledAsync(plugin, player.getUniqueId(), value);
            }
            player.sendMessage("§d" + e.getPlayer().getName() + "§eが§aSuper Afnw Ticket§eを使用し、アイテムが配布されました！");
        }
    }
}
