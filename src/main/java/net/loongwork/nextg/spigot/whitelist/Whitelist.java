package net.loongwork.nextg.spigot.whitelist;

import net.loongwork.nextg.spigot.utils.I18NUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Whitelist {
    private static final WhitelistRepo whitelistRepo = new WhitelistRepo();

    private Whitelist() {
    }

    public static void addPlayer(Player player) {
        whitelistRepo.add(new WhitelistUser(player.getUniqueId(), player.getName(), "manual", System.currentTimeMillis()));
        if (player.isOnline()) {
            player.kick(I18NUtils.getMessageComponent("whitelist.kick-for-refresh"));
        }
    }

    public static void removePlayer(Player player) {
        whitelistRepo.remove(player.getName());
        if (player.isOnline()) {
            player.kick(I18NUtils.getMessageComponent("whitelist.kick-for-refresh"));
        }
    }

    public static boolean containsPlayer(Player player) {
        return whitelistRepo.get(player.getName()) != null;
    }

    public static boolean containsPlayer(UUID uuid) {
        return whitelistRepo.get(Bukkit.getOfflinePlayer(uuid).getName()) != null;
    }

    public static boolean containsPlayer(String username) {
        return whitelistRepo.get(username) != null;
    }

    public static void reload() {
        whitelistRepo.load();
    }

    public static void shutdown() {
        whitelistRepo.save();
    }
}
