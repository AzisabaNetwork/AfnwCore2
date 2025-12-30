package net.azisaba.afnw.afnwcore2.commands;

import net.azisaba.afnw.afnwcore2.util.Expr;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SafeTeleportCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) {
            sender.sendMessage(Component.text("Player was not found", NamedTextColor.RED));
            return true;
        }
        int x;
        try {
            x = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            x = (int) Objects.requireNonNull(Expr.eval(player, args[1]));
        }
        int y;
        try {
            y = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            y = (int) Objects.requireNonNull(Expr.eval(player, args[2]));
        }
        int z;
        try {
            z = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            z = (int) Objects.requireNonNull(Expr.eval(player, args[3]));
        }
        Location location = new Location(player.getWorld(), x + 0.5, y, z + 0.5);
        player.teleport(location);
        location.subtract(0, 1, 0).getBlock().setType(Material.HONEYCOMB_BLOCK);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return null;
        }
        return Collections.emptyList();
    }
}
