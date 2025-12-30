package net.azisaba.afnw.afnwcore2.util.item;

import net.azisaba.itemstash.ItemStash;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public static @Nullable CompoundTag getCustomData(@NotNull ItemStack stack) {
        CustomData customData = CraftItemStack.asNMSCopy(stack).get(DataComponents.CUSTOM_DATA);
        if (customData == null) return null;
        return customData.copyTag();
    }

    public static @NotNull String getStringTag(@NotNull ItemStack stack, @NotNull String name) {
        CompoundTag tag = getCustomData(stack);
        if (tag == null) return "";
        return tag.getString(name).orElse("");
    }

    public static @NotNull CompoundTag getCompoundTag(@NotNull ItemStack stack, @NotNull String name) {
        CompoundTag tag = getCustomData(stack);
        if (tag == null) return new CompoundTag();
        return tag.getCompound(name).orElseGet(CompoundTag::new);
    }

    @Contract("null -> null")
    public static @Nullable String getMythicType(@Nullable ItemStack stack) {
        if (stack == null) return null;
        String s = getCompoundTag(stack, "PublicBukkitValues").getString("mythicmobs:type").orElse("");
        if (s.isBlank()) return null;
        return s;
    }

    public static @NotNull String toString(@NotNull ItemStack stack) {
        List<String> props = new ArrayList<>();
        props.add("[Type: " + stack.getType().name() + "]");
        props.add("[Amount: " + stack.getAmount() + "]");
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) props.add("[Name: " + meta.getDisplayName() + "]");
            if (meta.hasLore()) props.add("[Lore: " + Objects.requireNonNull(meta.getLore()).size() + " entries]");
            if (meta.hasCustomModelData()) props.add("[CustomModelData: " + meta.getCustomModelData() + "]");
            if (getMythicType(stack) != null) props.add("[MMID: " + getMythicType(stack) + "]");
            if (meta.hasEnchants()) meta.getEnchants().forEach((enchant, level) -> props.add("[Enchant: " + enchant.getKey() + " " + level + "]"));
        }
        return String.join("", props);
    }
}
