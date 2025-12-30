package net.azisaba.afnw.afnwcore2.commands;

import net.azisaba.afnw.afnwcore2.AfnwCore2;
import net.azisaba.afnw.afnwcore2.util.Expr;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MMGiveEvalCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        String src = String.join(" ", args);
        try {
            sender.sendMessage(String.valueOf(Expr.eval(player, src)));
        } catch (Exception e) {
            sender.sendMessage(Component.text(e.getClass().getName() + ": " + e.getMessage(), NamedTextColor.RED));
            AfnwCore2.getPluginLogger().error("Failed to evaluate script", e);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        try {
            return Expr.getSuggestions(sender, String.join(" ", args)).collect(Collectors.toList());
        } catch (Exception e) {
            String message = e.getMessage();
            return Collections.singletonList("§c" + message.substring(0, Math.min(150, message.length())));
        }
    }
}
