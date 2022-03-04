package net.loongwork.nextg.spigot.whitelist;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.internal.LinkedTreeMap;
import lombok.val;
import net.loongwork.nextg.spigot.NextGSpigot;
import net.loongwork.nextg.spigot.transport.HttpClient;
import net.loongwork.nextg.spigot.utils.I18NUtils;
import net.loongwork.nextg.spigot.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Whitelist {
    private static final WhitelistRepo whitelistRepo = new WhitelistRepo();

    private static boolean remoteWhitelist = false;

    private static LoadingCache<UUID, Boolean> cache = null;

    private Whitelist() {
    }

    public static void addPlayer(Player player) {
        whitelistRepo.add(new WhitelistUser(player.getUniqueId(), player.getName(), "manual", System.currentTimeMillis()));
        if (player.isOnline()) {
            player.kick(I18NUtils.getMessageComponent("whitelist.kick-for-refresh"));
        }
    }

    public static void removePlayer(Player player) {
        whitelistRepo.remove(player.getUniqueId());
        if (player.isOnline()) {
            player.kick(I18NUtils.getMessageComponent("whitelist.kick-for-refresh"));
        }
    }

    public static boolean containsPlayer(Player player) {
        return containsPlayer(player.getUniqueId());
    }

    public static boolean containsPlayer(UUID uuid) {
        return cache.getUnchecked(uuid);
    }

    public static void cleanPlayer(UUID uuid) {
        cache.invalidate(uuid);
    }

    public static void init() {
        whitelistRepo.load();
        remoteWhitelist = NextGSpigot.instance().getConfig().getBoolean("whitelist.remote", false);
        cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .expireAfterAccess(15, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Boolean load(@NotNull UUID key) {
                        if (remoteWhitelist) {
                            try {
                                val response = HttpClient.getService().getUser(PlayerUtils.uuidToName(key)).execute();
                                if (response.isSuccessful() && response.body() != null) {
                                    val user = (LinkedTreeMap) response.body().getData();
                                    return (Boolean) user.getOrDefault("in_whitelist", false);
                                }
                            } catch (IOException e) {
                                NextGSpigot.instance().getLogger().warning("远程白名单不可用，回退至本地白名单");
                            }
                        }
                        if (whitelistRepo.get(key) != null) {
                            return true;
                        }
                        return Bukkit.getWhitelistedPlayers().contains(Bukkit.getOfflinePlayer(key));
                    }
                });
    }

    public static void reload() {
        whitelistRepo.load();
    }

    public static void shutdown() {
        whitelistRepo.save();
    }
}
