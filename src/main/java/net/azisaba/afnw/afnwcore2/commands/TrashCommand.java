package net.azisaba.afnw.afnwcore2.commands;

import net.azisaba.afnw.afnwcore2.gui.TrashInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public record TrashCommand(JavaPlugin plugin) implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String s,
      String[] strings) {
    if (!(command.getName().equals("trash"))) {
      return false;
    }

    if (!(sender instanceof Player p)) {
      sender.sendMessage(Component.text("/trashコマンドはプレイヤーのみ実行可能です。", NamedTextColor.RED));
      return false;
    }

    if (!(sender.hasPermission("afnw.command.trash"))) {
      return false;
    }

    p.openInventory(new TrashInventory().getInventory());
    return true;
  }
}
