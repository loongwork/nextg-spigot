package net.loongwork.nextg.spigot.utils;

import co.aikar.commands.ACFUtil;
import co.aikar.commands.CommandManager;
import co.aikar.locales.MessageKey;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.loongwork.nextg.spigot.NextGSpigot;

import java.util.Locale;

public class I18NUtils {

    @Getter
    @Setter
    private static Locale globalLocale;

    public static String getMessage(String key) {
        return NextGSpigot.instance().getCommandManager().getLocales().getMessage(null, MessageKey.of(key));
    }

    public static String getMessage(String key, String... replacements) {
        return ACFUtil.replaceStrings(getMessage(key), replacements);
    }

    public static Component getMessageComponent(String key) {
        return Component.text(getMessage(key));
    }

    public static Component getMessageComponent(String key, String... replacements) {
        return Component.text(getMessage(key, replacements));
    }
}
