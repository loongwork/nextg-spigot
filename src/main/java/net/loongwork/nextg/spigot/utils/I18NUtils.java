package net.loongwork.nextg.spigot.utils;

import co.aikar.commands.ACFUtil;
import co.aikar.locales.MessageKey;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.loongwork.nextg.spigot.NextGSpigot;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class I18NUtils {

    @Getter
    @Setter
    private static Locale globalLocale;

    public static String getMessage(String key) {
        String msg = NextGSpigot.instance().getCommandManager().getLocales().getMessage(null, MessageKey.of(key));
        if (msg.matches("<(info|warn|error)>.*")) {
            Matcher matcher = Pattern.compile("<(info|warn|error)>").matcher(msg);
            while (matcher.find()) {
                String level = matcher.group(1);
                msg = msg.replace("<" + level + ">", NextGSpigot.instance().getConfig().getString("colors." + level, "<" + level + ">"));
                msg = msg.replace("</" + level + ">", "&r");
            }
        }
        return msg;
    }

    public static String getMessage(String key, String... replacements) {
        if (replacements.length == 0) {
            return getMessage(key);
        }
        return ACFUtil.replaceStrings(getMessage(key), replacements);
    }

    public static Component parseMessage(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public static Component getMessageComponent(String key) {
        return parseMessage(getMessage(key));
    }

    public static Component getMessageComponent(String key, String... replacements) {
        return parseMessage(getMessage(key, replacements));
    }
}
