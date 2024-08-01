package net.azisaba.afnw.afnwcore2;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import net.azisaba.afnw.afnwcore2.commands.*;
import net.azisaba.afnw.afnwcore2.listeners.block.CropsBreakCanceller;
import net.azisaba.afnw.afnwcore2.listeners.block.SaplingBreakCanceller;
import net.azisaba.afnw.afnwcore2.listeners.entity.DropShardListener;
import net.azisaba.afnw.afnwcore2.listeners.entity.WitherSpawn;
import net.azisaba.afnw.afnwcore2.listeners.other.VoteListener;
import net.azisaba.afnw.afnwcore2.listeners.player.*;
import net.azisaba.afnw.afnwcore2.util.TheTAB;
import net.azisaba.afnw.afnwcore2.util.data.PlayerData;
import net.azisaba.afnw.afnwcore2.util.data.PlayerDataSave;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftDolphin;
import org.bukkit.entity.Dolphin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * AfnwCore2 のメインクラス
 *
 * @author m2en
 * @see org.bukkit.plugin.java.JavaPlugin
 */
public class AfnwCore2 extends JavaPlugin {
  public final Set<UUID> pvpEnabled = new HashSet<>();

  @Override
  public void onEnable() {
    getLogger().info("起動開始...");

    // コンフィグのロード
    saveDefaultConfig();
    if (getConfig().getBoolean("settings.require-item-stash", false) && Bukkit.getPluginManager().getPlugin("ItemStash") == null) {
      throw new RuntimeException("ItemStashプラグインがインストールされていません。settings > require-item-stashをfalseにするか、ItemStashをインストール、またはエラーを確認してください。");
    }
    getLogger().info("コンフィグ ロード完了");

    // プレイヤーデータのロード
    PlayerData data = new PlayerData(this, "player-data.yml");
    data.saveDefaultPlayerData();
    getLogger().info("プレイヤーデータ ロード完了");

    // 自動保存の設定
    PlayerDataSave dataSchedule = new PlayerDataSave(this, data);
    dataSchedule.playerData();
    getLogger().info("プレイヤーデータ自動保存 設定完了");

    // Listenerの確定
    PluginManager pluginEvent = Bukkit.getPluginManager();
    getLogger().info("Listener 設定中....");
    /* プレイヤーリスナー */
    pluginEvent.registerEvents(new JoinListener(this, data), this);
    pluginEvent.registerEvents(new QuitListener(), this);
    pluginEvent.registerEvents(new DeathListener(), this);
    pluginEvent.registerEvents(new FirstPlayerJoinListener(this, data), this);
    pluginEvent.registerEvents(new AFKListener(this), this);
    pluginEvent.registerEvents(new RespawnEnvironment(this), this);
    pluginEvent.registerEvents(new SuperAfnwTicketListener(this), this);
    pluginEvent.registerEvents(new VillagerInteractListener(), this);
    pluginEvent.registerEvents(new VillagerProtectorListener(), this);
    pluginEvent.registerEvents(new BedListener(this), this);
    pluginEvent.registerEvents(new EnderDragonDisableListener(), this);
    pluginEvent.registerEvents(new PvPListener(this), this);
    pluginEvent.registerEvents(new BlessedRandomTeleporterListener(), this);
    /* エンティティリスナー */
    pluginEvent.registerEvents(new WitherSpawn(this), this);
    pluginEvent.registerEvents(new DropShardListener(), this);
    /* その他 */
    pluginEvent.registerEvents(new VoteListener(this, data), this);
    getLogger().info("Listener 設定完了");
    /* ブロックリスナー */
    pluginEvent.registerEvents(new CropsBreakCanceller(), this);
    pluginEvent.registerEvents(new SaplingBreakCanceller(), this);
    getLogger().info("Listener(Canceller) 設定完了");

    // コマンドの設定
    Objects.requireNonNull(getCommand("afnw")).setExecutor(new AfnwCommand(this, data));
    Objects.requireNonNull(getCommand("vote")).setExecutor(new VoteCommand());
    Objects.requireNonNull(getCommand("respawn")).setExecutor(new RespawnCommand());
    Objects.requireNonNull(getCommand("config_reload")).setExecutor(new ConfigReloadCommand(this));
    Objects.requireNonNull(getCommand("ticket")).setExecutor(new TicketCommand(this, data));
    Objects.requireNonNull(getCommand("lobby")).setExecutor(new LobbyCommand(this));
    Objects.requireNonNull(getCommand("void")).setExecutor(new VoidCommand(this));
    Objects.requireNonNull(getCommand("tutorial")).setExecutor(new TutorialCommand(this));
    Objects.requireNonNull(getCommand("bed")).setExecutor(new BedCommand(this));
    Objects.requireNonNull(getCommand("pc")).setExecutor(new EnderchestCommand());
    Objects.requireNonNull(getCommand("trash")).setExecutor(new TrashCommand(this));
    Objects.requireNonNull(getCommand("maintenance")).setExecutor(new MaintenanceCommand());
    Objects.requireNonNull(getCommand("bonus")).setExecutor(new BonusCommand(this, data));
    Objects.requireNonNull(getCommand("pvp")).setExecutor(new PvPCommand(this));
    Objects.requireNonNull(getCommand("mmgive")).setExecutor(new MMGiveCommand(this));
    Objects.requireNonNull(getCommand("mmgiveeval")).setExecutor(new MMGiveEvalCommand());
    Objects.requireNonNull(getCommand("safeteleport")).setExecutor(new SafeTeleportCommand());
    getLogger().info("コマンド 設定完了");

    Bukkit.getScheduler().runTask(this, () -> {
      if (Bukkit.getPluginManager().isPluginEnabled("TAB")) {
        TheTAB.enable();
        getLogger().info("TABの連携が有効になりました。");
      } else {
        getLogger().info("TABの連携は無効です。");
      }
    });

    if(getConfig().getBoolean("settings.maintenance-mode-toggle", false)) {
      getServer().setWhitelist(true);
      getLogger().info("正常に起動しました。(メンテナンスモード)");
      return;
    }

    Bukkit.getScheduler().runTaskTimer(this, () -> {
      for (World world : Bukkit.getWorlds()) {
        for (Dolphin entity : world.getEntitiesByClass(Dolphin.class)) {
          // Prevent DolphinSwimToTreasureGoal from being triggered
          ((CraftDolphin) entity).getHandle().goalSelector.removeAllGoals(goal -> goal.getClass().getTypeName().equals("net.minecraft.world.entity.animal.EntityDolphin$a"));
        }
      }
    }, 10, 10);

    ((RangedAttribute) Attributes.LUCK).maxValue = Double.MAX_VALUE;
    getLogger().info("正常に起動しました。");
  }

  @Override
  public void onDisable() {
    if(getConfig().getBoolean("settings.maintenance-mode-toggle", false)) {
      getServer().setWhitelist(false);
      getLogger().info("正常に終了しました。(メンテナンスモード)");
    }
    getLogger().info("正常に終了しました。");
  }

  @NotNull
  public static Logger getPluginLogger() {
    return getPlugin(AfnwCore2.class).getSLF4JLogger();
  }
}
