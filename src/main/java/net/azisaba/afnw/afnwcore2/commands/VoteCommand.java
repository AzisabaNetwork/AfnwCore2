package net.azisaba.afnw.afnwcore2.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * /vote - Submit list of voting sites
 *
 * @author m2en
 * @see org.bukkit.command.CommandExecutor
 */
public class VoteCommand implements CommandExecutor {

  /**
   * /vote
   *
   * @param sender  Source of the command
   * @param command Command which was executed
   * @param label   Alias of the command which was used
   * @param args    Passed command arguments
   * @return Result of command execution
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {
    if (!(command.getName().equals("vote"))) {
      return true;
    }
    if (!(sender.hasPermission("afnw.command.vote"))) {
      return true;
    }

    String[] voteSiteURLs = {
        ChatColor.GOLD +
            """
                投票よろしくお願いします!
                ・JMS
                https://minecraft.jp/servers/azisaba.net
                """
    };

    sender.sendMessage(voteSiteURLs);
    return true;
  }
}
