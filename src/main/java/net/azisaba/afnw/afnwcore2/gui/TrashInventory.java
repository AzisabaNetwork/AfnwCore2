package net.azisaba.afnw.afnwcore2.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class TrashInventory implements InventoryHolder {
    public static final ItemStack trashItem = new ItemStack(Material.BARRIER);
    private final Inventory inventory = Bukkit.createInventory(this, 54, Component.text("ゴミ箱", NamedTextColor.RED));

    static {
        ItemMeta meta = trashItem.getItemMeta();
        meta.displayName(Component.text("ゴミを捨てる", NamedTextColor.RED));
        trashItem.setItemMeta(meta);
    }

    public TrashInventory() {
        inventory.setItem(53, trashItem);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
