package net.azisaba.afnw.afnwcore2.commands;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.azisaba.afnw.afnwcore2.util.data.PlayerData;
import net.azisaba.afnw.afnwcore2.util.item.AfnwScaffold;
import net.azisaba.afnw.afnwcore2.util.item.AfnwTicket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * コマンド "afnw" の実装レコードです。
 *
 * @author m2en
 * @see org.bukkit.command.CommandExecutor
 */
public record AfnwCommand(JavaPlugin plugin, PlayerData playerData) implements CommandExecutor {

  private static boolean isAllowed(Material type) {
      if (!type.isItem()) {
          return false;
      }
      return switch (type) {
          case BEDROCK, STRUCTURE_BLOCK, STRUCTURE_VOID, COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, COMMAND_BLOCK_MINECART,
                  REPEATING_COMMAND_BLOCK, BARRIER, LIGHT, JIGSAW, END_PORTAL, KNOWLEDGE_BOOK, DEBUG_STICK,
                  AIR, VOID_AIR, CAVE_AIR, BUNDLE -> false;
          default -> true;
      };
  }

  @Contract("_ -> new")
  public static @NotNull ItemStack getRandomItem(int amount) {
    return getRandomItem(0, amount);
  }

  @Contract("_, _ -> new")
  public static @NotNull ItemStack getRandomItem(int luck, int amount) {
    try {
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      List<Material> itemList = new ArrayList<>(Arrays.asList(Material.values()));
      itemList.removeIf(type -> !isAllowed(type));
      if (luck > 0) {
        for (int i = 0; i < luck; i++) {
          itemList.add(Material.DRIPSTONE_BLOCK);
          itemList.add(Material.POINTED_DRIPSTONE);
          itemList.add(Material.LAVA_BUCKET);
          itemList.add(Material.DIRT);
          itemList.add(Material.NETHERITE_BLOCK);
          itemList.add(Material.DIAMOND_BLOCK);
          itemList.add(Material.GOLD_BLOCK);
          itemList.add(Material.ELYTRA);
          itemList.add(Material.END_PORTAL_FRAME);
          itemList.add(Material.ENDER_PEARL);
          itemList.add(Material.ENDER_EYE);
          itemList.add(Material.TRIDENT);
          itemList.add(Material.ENCHANTED_GOLDEN_APPLE);
          itemList.add(Material.NETHER_STAR);
          itemList.add(Material.ANCIENT_DEBRIS);
          itemList.add(Material.BLAZE_POWDER);
          itemList.add(Material.SPAWNER);
          itemList.add(Material.NETHERITE_INGOT);
          itemList.add(Material.DIAMOND);
          itemList.add(Material.GOLD_INGOT);
          itemList.add(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        }
      }
      return new ItemStack(itemList.get(random.nextInt(itemList.size() - 1)), amount);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

  }

  /**
   * /afnw
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
    if (!(command.getName().equals("afnw"))) {
      return true;
    }
    if (!(sender instanceof Player)) {
      sender.sendMessage(Component.text("/afnwコマンドはプレイヤーのみ実行可能です。").color(NamedTextColor.RED));
      return true;
    }
    if (!(sender.hasPermission("afnw.command.afnw"))) {
      return true;
    }

    Inventory inv = ((Player) sender).getInventory();
    int firstInv = inv.firstEmpty();
    if (firstInv == -1) {
      sender.sendMessage(
          Component.text("インベントリに空きがありません。十分な空きを作ってから交換してください。").color(NamedTextColor.RED));
      return true;
    }
    if (!inv.containsAtLeast(AfnwTicket.afnwTicket, 1)) {
      sender.sendMessage(
          Component.text("チケットが見つかりません。インベントリにチケットがあるか確認してください。").color(NamedTextColor.RED));
      return true;
    }

    FileConfiguration config = plugin.getConfig();
    int itemSize = config.getInt("vote.item-size", 1);
    int scaffoldSize = config.getInt("vote.scaffold-size", 8);

    int luck = (int) Math.ceil(Optional.ofNullable(((Player) sender).getAttribute(Attribute.GENERIC_LUCK)).map(AttributeInstance::getValue).orElse(0.0));
    ItemStack afnwItem = getRandomItem(luck, itemSize);

    inv.removeItem(AfnwTicket.afnwTicket);
    inv.addItem(afnwItem);
    for (int i = 0; i < scaffoldSize; i++) {
      inv.addItem(AfnwScaffold.afnwScaffold);
    }

    sender.sendMessage(Component.text("アイテムと交換しました。").color(NamedTextColor.GOLD));
    sender.sendMessage(Component.text(
            "交換内容: " + afnwItem.getType() + " ×" + afnwItem.getAmount() + ", 足場ブロック ×" + scaffoldSize)
        .color(NamedTextColor.GOLD));
    return true;
  }
}
