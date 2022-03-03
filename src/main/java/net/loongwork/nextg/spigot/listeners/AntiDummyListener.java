package net.loongwork.nextg.spigot.listeners;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.loongwork.nextg.spigot.NextGSpigot;
import net.loongwork.nextg.spigot.transport.SocketClient;
import net.loongwork.nextg.spigot.events.SocketMessageEvent;
import net.loongwork.nextg.spigot.transport.packets.BasePacket;
import net.loongwork.nextg.spigot.utils.I18NUtils;
import net.loongwork.nextg.spigot.whitelist.Whitelist;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AntiDummyListener implements Listener {

    private final LoadingCache<UUID, Integer> loginAttemptsPerPlayer = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build(new CacheLoader<>() {
        @Override
        public Integer load(@NotNull UUID key) {
            return 0;
        }
    });

    @EventHandler
    public void onPlayerAsyncPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (Whitelist.containsPlayer(event.getUniqueId())) {
            return;
        }

        if (NextGSpigot.instance().getConfig().getBoolean("anti-dummy.strict", false)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, I18NUtils.getMessageComponent("anti-dummy.kick-strict-mode"));
        }

        if (isGlobalMaximumAttemptsExceeded()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, I18NUtils.getMessageComponent("anti-dummy.kick-by-throttle"));
        }

        if (isMaximumAttemptsPerPlayerExceeded(event.getUniqueId())) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, I18NUtils.getMessageComponent("anti-dummy.kick-too-many-attempts"));
        }

        loginAttemptsPerPlayer.put(event.getUniqueId(), loginAttemptsPerPlayer.getUnchecked(event.getUniqueId()) + 1);
    }

    private boolean isGlobalMaximumAttemptsExceeded() {
        return loginAttemptsPerPlayer.size() >= 10;
    }

    private boolean isMaximumAttemptsPerPlayerExceeded(UUID uuid) {
        return loginAttemptsPerPlayer.getUnchecked(uuid) >= 5;
    }
}
