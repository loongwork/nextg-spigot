package net.loongwork.nextg.spigot.commands;

import co.aikar.commands.MessageType;
import co.aikar.locales.MessageKey;
import net.loongwork.nextg.spigot.Constants;
import net.loongwork.nextg.spigot.utils.I18NUtils;
import org.bukkit.entity.Player;

public class BaseCommand extends co.aikar.commands.BaseCommand {
    static String key(String key) {
        return Constants.ACF_BASE_KEY + "." + key;
    }

    protected void reply(String key, String... replacements) {
        getCurrentCommandIssuer().sendMessageInternal(I18NUtils.getMessage(key(key), replacements));
    }
}
