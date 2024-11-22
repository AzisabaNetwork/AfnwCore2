package net.azisaba.afnw.afnwcore2.listeners.player;

import net.azisaba.afnw.afnwcore2.AfnwCore2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * ログアウトしたプレイヤーに関するクラス
 *
 * @author m2en
 * @see org.bukkit.event.Listener
 */
public class QuitListener implements Listener {
  private final @NotNull AfnwCore2 plugin;

  public QuitListener(@NotNull AfnwCore2 plugin) {
    this.plugin = plugin;
  }

  /**
   * ログアウト通知を行います。
   *
   * @param e Target player for PlayerQuitEvent
   */
  @EventHandler(priority = EventPriority.NORMAL)
  public void onQuit(PlayerQuitEvent e) {
    // ログアウト通知を行う
    Player p = e.getPlayer();

    e.quitMessage(Component.text("* " + p.getName() + "がログアウトしました。").color(NamedTextColor.AQUA));
    plugin.pvpEnabled.remove(p.getUniqueId());
  }
}
