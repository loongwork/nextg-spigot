package net.loongwork.nextg.spigot.utils;

import me.lucko.helper.Schedulers;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.concurrent.Callable;

public class MessageUtils {

    public static class Title {
        public static void show(Player player, Component title, Component subtitle) {
            player.showTitle(net.kyori.adventure.title.Title.title(title, subtitle));
        }

        public static void show(Player player, Component title) {
            show(player, title, Component.empty());
        }

        public static void show(Player player, String title, String subtitle) {
            show(player, I18NUtils.getMessageComponent(title), I18NUtils.getMessageComponent(subtitle));
        }

        public static void show(Player player, String title) {
            show(player, I18NUtils.getMessageComponent(title));
        }
    }

    public static class ActionBar {
        public static void show(Player player, Component message) {
            player.sendActionBar(message);
        }

        public static void show(Player player, String message) {
            show(player, I18NUtils.getMessageComponent(message));
        }

        public static void showUntil(Player player, Component message, Callable<Boolean> condition) {
            Schedulers.async().runRepeating(task -> {
                try {
                    if (condition.call()) {
                        task.stop();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                show(player, message);
            }, 0L, 40L);
        }

        public static void showUntil(Player player, String message, Callable<Boolean> condition) {
            showUntil(player, I18NUtils.getMessageComponent(message), condition);
        }
    }
}
