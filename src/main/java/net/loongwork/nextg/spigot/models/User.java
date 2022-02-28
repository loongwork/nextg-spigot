package net.loongwork.nextg.spigot.models;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.val;
import me.lucko.helper.metadata.Metadata;
import net.loongwork.nextg.spigot.Constants;
import net.loongwork.nextg.spigot.NextGSpigot;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public record User(String username, Player player) {

    private static LoadingCache<UUID, Optional<User>> cachedUsers;

    public static void init() {
        cachedUsers = CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterAccess(15, TimeUnit.MINUTES).build(new CacheLoader<>() {
            @Override
            public Optional<User> load(@NotNull UUID key) {
                NextGSpigot.instance().getLogger().info("正在加载玩家信息至缓存：" + key);
                val player = Bukkit.getOfflinePlayer(key);
                if (player.hasPlayedBefore()) {
                    return Optional.of(new User(player.getName(), player.getPlayer()));
                }
                return Optional.empty();
            }
        });
    }

    public static User get(UUID uuid) {
        return cachedUsers.getUnchecked(uuid).orElse(null);
    }

    public static User get(Player player) {
        return get(player.getUniqueId());
    }

    public static User get(OfflinePlayer offlinePlayer) {
        return get(offlinePlayer.getUniqueId());
    }

    public void markAsVisitor() {
        Metadata.provideForPlayer(player).put(Constants.PLAYER_IS_VISITOR, true);
    }

    public Boolean isVisitor() {
        return Metadata.provideForPlayer(player).getOrDefault(Constants.PLAYER_IS_VISITOR, false);
    }
}
