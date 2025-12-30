package net.azisaba.afnw.afnwcore2.listeners.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * 死亡したプレイヤーに関するクラス
 *
 * @author m2en
 * @see org.bukkit.event.Listener
 */
public class DeathListener implements Listener {

    /**
     * 死んだプレイヤーに失った経験治療を通知する
     *
     * @param e イベント発火原因のプレイヤー
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();

        String inventoryMessage;
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            e.setKeepInventory(true);
            e.getDrops().clear();
            inventoryMessage = "サバイバルモード以外のため、インベントリの中身は失いません。";
        } else {
            if (e.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) >= 20 * 60 * 60 * 5) { // 5 hours
                e.setKeepInventory(false);
                inventoryMessage = "プレイ時間が5時間以上のため、インベントリの中身を失いました。";
            } else {
                e.setKeepInventory(true);
                e.getDrops().clear();
                inventoryMessage = "プレイ時間が5時間未満のため、インベントリの中身は失いません。";
            }
        }

        // ドロップした経験値量を通知
        if (e.getDroppedExp() == 0) {
            p.sendMessage(Component.text("死亡しました。経験値の消費はありません。" + inventoryMessage, NamedTextColor.RED));
        } else {
            p.sendMessage(
                    Component.text("死亡したため、" + e.getDroppedExp() + " Expを失いました。" + inventoryMessage, NamedTextColor.RED));
        }
    }
}
