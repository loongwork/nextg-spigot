package net.loongwork.nextg.spigot.utils;

import net.loongwork.nextg.spigot.NextGSpigot;
import org.bukkit.entity.Player;

public class PlayerUtils {
    public static String getPrefixedName(Player player) {
        return NextGSpigot.instance().getVault().getPlayerPrefix(player) + player.getName();
    }
}
