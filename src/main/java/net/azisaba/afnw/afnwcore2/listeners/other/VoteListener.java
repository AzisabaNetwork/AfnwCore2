package net.azisaba.afnw.afnwcore2.listeners.other;

import com.vexsoftware.votifier.model.VotifierEvent;
import net.azisaba.afnw.afnwcore2.AfnwCore2;
import net.azisaba.afnw.afnwcore2.util.data.PlayerData;
import net.azisaba.afnw.afnwcore2.util.item.AfnwTicket;
import net.azisaba.afnw.afnwcore2.util.item.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public record VoteListener(AfnwCore2 plugin, PlayerData playerData) implements Listener {
    @EventHandler
    public void onVote(VotifierEvent e) {
        handleVote(e);
        handleVote(e);
    }

    public void handleVote(VotifierEvent e) {
        FileConfiguration config = plugin.getConfig();
        int ticketSize = config.getInt("vote.send-ticket-size", 1);
        int bonusLine = config.getInt("vote.bonus-line", 9);
        Player sendTarget = Bukkit.getPlayerExact(e.getVote().getUsername());

        ItemStack ticketItem = AfnwTicket.afnwTicket.clone();
        ticketItem.setAmount(ticketSize);

        Bukkit.broadcast(
                Component.text("[", NamedTextColor.GOLD)
                        .append(Component.text("Broadcast", NamedTextColor.DARK_RED))
                        .append(Component.text("] ", NamedTextColor.GOLD))
                        .append(Component.text(e.getVote().getUsername(), NamedTextColor.RED))
                        .append(Component.text("が" + e.getVote().getServiceName() + "で投票しました！", NamedTextColor.DARK_GREEN)));
        if (sendTarget != null) {
            Inventory inv = sendTarget.getInventory();

            for (ItemStack stack : inv.addItem(ticketItem).values()) {
                ItemUtil.addToStashIfEnabledAsync(plugin, sendTarget.getUniqueId(), stack);
            }

            FileConfiguration dataFile = playerData.getPlayerData();
            int voteCount = dataFile.getInt((sendTarget.getUniqueId().toString()));
            voteCount++;
            if (voteCount >= bonusLine) {
                for (int i = 0; i < 10; i++) {
                    for (ItemStack value : inv.addItem(AfnwTicket.afnwTicket).values()) {
                        ItemUtil.addToStashIfEnabledAsync(plugin, sendTarget.getUniqueId(), value);
                    }
                }
                ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
                ItemMeta meta = netherStar.getItemMeta();
                meta.displayName(Component.text("投票ボーナス", NamedTextColor.YELLOW));
                netherStar.setItemMeta(meta);
                for (ItemStack value : inv.addItem(netherStar).values()) {
                    ItemUtil.addToStashIfEnabledAsync(plugin, sendTarget.getUniqueId(), value);
                }
                sendTarget.sendMessage(Component.text("* 投票ボーナスとしてチケット10枚とネザースターを獲得しました。")
                        .color(NamedTextColor.YELLOW));
                sendTarget.sendMessage(
                        Component.text("* 投票ボーナスがリセットされました。次回以降の投票から有効です。").color(NamedTextColor.YELLOW));
                plugin.getSLF4JLogger().info(sendTarget.getName() + "が投票ボーナスを獲得しました。");
                voteCount = 0;
            }
            dataFile.set(sendTarget.getUniqueId().toString(), voteCount);
            playerData.savePlayerData();

            // 成功した趣旨の情報送信
            plugin.getSLF4JLogger().info(sendTarget.getName() + "へのチケット送信に成功しました。配布数:" + ticketSize);
            // 通知
            sendTarget.sendMessage(Component.text("チケットを入手しました。/afnwを実行することでアイテムと交換することができます。",
                    NamedTextColor.LIGHT_PURPLE));
            sendTarget.sendMessage(Component.text("投票ボーナスまで: " + voteCount + "/" + config.getInt("vote.bonus-line", 10), NamedTextColor.YELLOW));
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                OfflinePlayer player = Bukkit.getOfflinePlayer(e.getVote().getUsername());
                ItemUtil.addToStashIfEnabled(player.getUniqueId(), ticketItem);
                FileConfiguration dataFile = playerData.getPlayerData();
                int voteCount = dataFile.getInt((player.getUniqueId().toString()));
                voteCount++;
                if (voteCount >= bonusLine) {
                    for (int i = 0; i < 10; i++) {
                        ItemUtil.addToStashIfEnabled(player.getUniqueId(), AfnwTicket.afnwTicket);
                    }
                    ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
                    ItemMeta meta = netherStar.getItemMeta();
                    meta.displayName(Component.text("投票ボーナス", NamedTextColor.YELLOW));
                    netherStar.setItemMeta(meta);
                    ItemUtil.addToStashIfEnabled(player.getUniqueId(), netherStar);
                    plugin.getSLF4JLogger().info(player.getName() + " (" + player.getUniqueId() + ")が投票ボーナスを獲得しました。");
                    voteCount = 0;
                }
                dataFile.set(player.getUniqueId().toString(), voteCount);
                playerData.savePlayerData();

                // 成功した趣旨の情報送信
                plugin.getSLF4JLogger().info(player.getName() + " (" + player.getUniqueId() + ")へのチケット送信に成功しました。配布数:" + ticketSize);
            });
        }
    }
}
