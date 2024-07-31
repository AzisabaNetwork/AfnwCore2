package net.azisaba.afnw.afnwcore2.util.item;

import net.azisaba.itemstash.ItemStash;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ItemUtil {
    public static void addToStashIfEnabled(@NotNull UUID uuid, @NotNull ItemStack stack) {
        if (Bukkit.getPluginManager().isPluginEnabled("ItemStash")) {
            ItemStash.getInstance().addItemToStash(uuid, stack);
        }
    }

    public static void addToStashIfEnabledAsync(@NotNull Plugin plugin, @NotNull UUID uuid, @NotNull ItemStack stack) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> addToStashIfEnabled(uuid, stack));
    }

    public static @NotNull String getStringTag(@NotNull ItemStack stack, @NotNull String name) {
        return CraftItemStack.asNMSCopy(stack).getOrCreateTag().getString(name);
    }

    @Contract("null -> null")
    public static @Nullable String getMythicType(@Nullable ItemStack stack) {
        if (stack == null) return null;
        String s = getStringTag(stack, "MYTHIC_TYPE");
        if (s.isBlank()) return null;
        return s;
    }
}
