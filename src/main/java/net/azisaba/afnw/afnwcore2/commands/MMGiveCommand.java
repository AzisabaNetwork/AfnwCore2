package net.azisaba.afnw.afnwcore2.commands;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import net.azisaba.afnw.afnwcore2.util.Expr;
import net.azisaba.afnw.afnwcore2.util.item.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public record MMGiveCommand(@NotNull Plugin plugin) implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) {
            sender.sendMessage(Component.text("Player " + args[0] + " is offline (tried to give " + args[1] + " (extra: " + String.join(", ", args) + "))", NamedTextColor.RED));
            return true;
        }
        String mythicType = args[1];
        int amount = args.length == 2 ? 1 : Integer.parseInt(args[2]);
        ItemStack stack = MythicBukkit.inst().getItemManager().getItemStack(mythicType, amount);
        if (stack.hasItemMeta()) {
            BiFunction<Component, String, Component> modify = new BiFunction<>() {
                @NotNull
                @Override
                public Component apply(Component component, String description) {
                    if (component instanceof TextComponent text) {
                        String replaced = Expr.evalAndReplace(player, text.content(), description);
                        if (replaced != null) {
                            component = text.content(replaced);
                        }
                    }
                    return component.children(component.children().stream().map(c -> apply(c, description)).toList());
                }
            };
            ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());
            if (meta.hasLore()) {
                meta.lore(Objects.requireNonNull(meta.lore()).stream().map(line -> modify.apply(line, mythicType + " (lore)")).toList());
            }
            if (meta.hasDisplayName()) {
                meta.displayName(modify.apply(meta.displayName(), mythicType + " (name)"));
            }
            stack.setItemMeta(meta);
        }
        for (ItemStack value : player.getInventory().addItem(stack).values()) {
            ItemUtil.addToStashIfEnabledAsync(plugin, player.getUniqueId(), value);
            player.sendMessage(Component.text("インベントリがいっぱいのため、Stashに入りました。", NamedTextColor.RED));
            player.sendMessage(
                    Component.text("/pickupstashで受け取れます。", NamedTextColor.RED)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/pickupstash"))
                            .hoverEvent(HoverEvent.showText(Component.text("クリックでStashの画面を開く")))
            );
        }
        return true;
    }

    @NotNull
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[0])).toList();
        }
        if (args.length == 2) {
            return MythicBukkit.inst()
                    .getItemManager()
                    .getItems()
                    .stream()
                    .map(MythicItem::getInternalName).filter(s -> s.startsWith(args[1]))
                    .toList();
        }
        return Collections.emptyList();
    }
}
