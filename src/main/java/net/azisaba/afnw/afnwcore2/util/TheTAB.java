package net.azisaba.afnw.afnwcore2.util;

import net.azisaba.afnw.afnwcore2.AfnwCore2;
import net.azisaba.tabbukkitbridge.data.DataKey;
import org.bukkit.entity.Player;

public class TheTAB {
    private static boolean enabled = false;

    public static void enable() {
        if (enabled) return;
        enabled = true;
        try {
            DataKey<Player, Boolean> pvpEnabled = new DataKey<>(false);
            pvpEnabled.register(p -> true, player -> {
                if (player == null) return false;
                return AfnwCore2.getPlugin(AfnwCore2.class).pvpEnabled.contains(player.getUniqueId());
            });
            pvpEnabled.getPlaceholders().add("pvp_enabled");
        } catch (Exception | NoClassDefFoundError e) {
            e.printStackTrace();
        }
    }
}
