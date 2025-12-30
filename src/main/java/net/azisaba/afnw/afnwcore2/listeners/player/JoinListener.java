package net.azisaba.afnw.afnwcore2.listeners.player;

import java.time.Duration;
import java.util.Arrays;
import net.azisaba.afnw.afnwcore2.AfnwCore2;
import net.azisaba.afnw.afnwcore2.util.data.PlayerData;
import net.azisaba.afnw.afnwcore2.util.item.AfnwTicket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * サーバーに参加したプレイヤーに関するクラスです。
 *
 * @author m2en
 * @see org.bukkit.event.Listener
 */
public record JoinListener(JavaPlugin plugin, PlayerData playerData) implements Listener {


  /**
   * 参加したプレイヤーのログインメッセージなど
   *
   * @param e Target player for PlayerJoinEvent
   */
  @EventHandler(priority = EventPriority.NORMAL)
  public void onJoin(PlayerJoinEvent e) {
    Player p = e.getPlayer();

    // 初参加の場合
    if (!playerData.getPlayerData().getBoolean("players." + p.getUniqueId() + ".first-join", false)) {
      playerData.getPlayerData().set("players." + p.getUniqueId() + ".first-join", true);
      Bukkit.getScheduler().runTaskAsynchronously(plugin, playerData::savePlayerData);
    }

    // プレイヤーがサーバーに参加したらタイトルとログインメッセージを送信する
    sendPlayerTitle(p);
    e.joinMessage(Component.text("* " + p.getName() + "がログインしました").color(NamedTextColor.AQUA));

    // 統合版のプレイヤーをブロックする(Bedrock Blocker)
    FileConfiguration config = plugin.getConfig();
    String[] allowPlayerId = config.getStringList("settings.allow-bedrock-player").toArray(new String[0]);

    if (p.getName().startsWith(".")) {
        if (p.getName().equals(Arrays.toString(allowPlayerId))) {
          p.sendMessage(Component.text("Bedrock Blockerの発動を解除しました").color(NamedTextColor.YELLOW));
          return;
        }
      p.sendMessage(Component.text(
          "Minecraft Bedrock Edition(統合版)での接続を検知しました。\n大変申し訳ありませんが統合版でAfnwをプレイすることはできません。Java版での接続をお願いします。15秒後自動的にアジ鯖ロビーへ戻ります。",
          NamedTextColor.RED));

      new BukkitRunnable() {
        @Override
        public void run() {
          Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
              "ekick " + p.getName() + "Bedrock Blocker // 統合版での接続を検知したため、接続を切断しました。");
        }
      }.runTaskLater(JavaPlugin.getPlugin(AfnwCore2.class), 20L * 15);
    }
  }

  /**
   * Methods to generate and send titles using Adventure
   *
   * @param p Player to send title
   * @deprecated We plan to remove this method in the future
   */
  @Deprecated
  public void sendPlayerTitle(@NonNull Player p) {
    // タイトルの表示時間の設定
    final Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000),
        Duration.ofMillis(1000));

    // タイトルの設定
    final Title title = Title.title(Component.text("Afnwへようこそ!").color(NamedTextColor.AQUA),
        Component.empty(), times);

    // タイトルの表示
    p.showTitle(title);
  }
}
