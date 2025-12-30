package net.azisaba.afnw.afnwcore2.listeners.player;

import net.azisaba.afnw.afnwcore2.AfnwCore2;
import net.azisaba.afnw.afnwcore2.gui.TrashInventory;
import net.azisaba.afnw.afnwcore2.util.item.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record TrashListener(@NotNull AfnwCore2 plugin) implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof TrashInventory)) {
            return;
        }
        if (e.getSlot() != 53) {
            return;
        }
        e.getInventory().setItem(53, null);
        int count = 0;
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : e.getInventory().getContents()) {
            if (item == null || item.getAmount() <= 0) continue;
            count += item.getAmount();
            items.add(item.clone());
            item.setAmount(0);
        }
        e.getWhoClicked().sendMessage(Component.text("ゴミ箱に" + count + "個のアイテムを捨てました。", NamedTextColor.YELLOW));
        plugin.getSLF4JLogger().info("Player {} has trashed {} items:", e.getWhoClicked().getName(), count);
        for (ItemStack item : items) {
            plugin.getSLF4JLogger().info("  {}", ItemUtil.toString(item));
            e.getWhoClicked().sendMessage(Component.text("  " + ItemUtil.toString(item), NamedTextColor.GRAY));
        }
        e.getClickedInventory().clear();
        e.getWhoClicked().closeInventory();
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof TrashInventory) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof TrashInventory)) {
            return;
        }
        e.getInventory().setItem(53, null);
        boolean stash = false;
        for (ItemStack item : e.getInventory().getContents()) {
            if (item == null || item.getAmount() <= 0) continue;
            for (ItemStack value : e.getPlayer().getInventory().addItem(item).values()) {
                ItemUtil.addToStashIfEnabledAsync(plugin, e.getPlayer().getUniqueId(), value);
                stash = true;
            }
        }
        if (stash) {
            e.getPlayer().sendMessage(Component.text("インベントリがいっぱいのため、Stashに保管されました。", NamedTextColor.RED));
            e.getPlayer().sendMessage(Component.text("/pickupstash", NamedTextColor.AQUA).append(Component.text("で回収できます。", NamedTextColor.RED)));
        }
    }
}
