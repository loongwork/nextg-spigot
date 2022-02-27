package net.loongwork.nextg.spigot.whitelist;

import org.bukkit.entity.Player;

public class Whitelist {
    private static final WhitelistRepo whitelistRepo = new WhitelistRepo();

    private Whitelist() {
    }

    public static void addPlayer(Player player) {
        whitelistRepo.add(new WhitelistUser(player.getUniqueId(), player.getName(), "manual", System.currentTimeMillis()));
    }

    public static void removePlayer(Player player) {
        whitelistRepo.remove(player.getUniqueId());
    }

    public static void reload() {
        whitelistRepo.load();
    }

    public static void shutdown() {
        whitelistRepo.save();
    }
}
