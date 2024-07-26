package net.azisaba.afnw.afnwcore2.util.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * プレイヤーデータの自動保存を行うクラスです。
 *
 * @param plugin     メインクラス
 * @param playerData プレイヤーデータクラス
 */
public record PlayerDataSave(JavaPlugin plugin, PlayerData playerData) {

  /**
   * コンフィグで指定された秒数間隔でプレイヤーデータを保存します。
   *
   * @return プレイヤーデータ
   */
  public PlayerData playerData() {
    int setPeriod = plugin.getConfig().getInt("settings.player-save-period", 120);
    new BukkitRunnable() {
      @Override
      public void run() {
        playerData.savePlayerData();
        playerData.reloadPlayerData();
      }
    }.runTaskTimer(plugin, 0, 20L * setPeriod);
    return playerData;
  }
}
