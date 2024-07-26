package net.azisaba.afnw.afnwcore2.util;

import net.azisaba.afnw.afnwcore2.AfnwCore2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class TheTAB {
    private static boolean enabled = false;

    public static void enable() {
        try {
            registerPlaceholder("%pvp_enabled%", 1000, player -> AfnwCore2.getPlugin(AfnwCore2.class).pvpEnabled.contains(player.getUniqueId()));
            Class.forName("me.neznamy.tab.shared.placeholders.conditions.Condition").getMethod("finishSetups").invoke(null);
            if (enabled) return;
            Object tabAPI = Class.forName("me.neznamy.tab.api.TabAPI").getMethod("getInstance").invoke(null);
            Class.forName("me.neznamy.tab.api.event.EventBus")
                    .getMethod("register", Class.class, Consumer.class)
                    .invoke(tabAPI, Class.forName("me.neznamy.tab.api.event.plugin.TabLoadEvent"), (Consumer<Object>) (e) -> enable());
            enabled = true;
        } catch (ReflectiveOperationException e) {
            AfnwCore2.getPluginLogger().warn("Failed to execute Condition#finishSetups", e);
        }
    }

    private static void registerPlaceholder(String identifier, int refreshInterval, @NotNull Function<Player, Object> function) throws ReflectiveOperationException {
        Object tabAPI = Class.forName("me.neznamy.tab.api.TabAPI").getMethod("getInstance").invoke(null);
        Object placeholderManager = Class.forName("me.neznamy.tab.api.TabAPI").getMethod("getPlaceholderManager").invoke(tabAPI);
        Class.forName("me.neznamy.tab.api.placeholder.PlaceholderManager")
                .getMethod("registerPlayerPlaceholder", String.class, int.class, Function.class)
                .invoke(placeholderManager, identifier, refreshInterval, (Function<Object, Object>) (obj) -> {
                    try {
                        return function.apply((Player) Class.forName("me.neznamy.tab.api.TabPlayer").getMethod("getPlayer").invoke(obj));
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
