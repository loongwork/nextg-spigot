package net.loongwork.nextg.spigot.commands;

import co.aikar.commands.MessageType;
import co.aikar.locales.MessageKey;
import net.loongwork.nextg.spigot.Constants;

public class BaseCommand extends co.aikar.commands.BaseCommand {
    static MessageKey key(String key) {
        return MessageKey.of(Constants.ACF_BASE_KEY + "." + key);
    }

    protected void success(String key, String... replacements) {
        getCurrentCommandIssuer().sendMessage(MessageType.INFO, key(key), replacements);
    }

    protected void error(String key, String... replacements) {
        getCurrentCommandIssuer().sendMessage(MessageType.ERROR, key(key), replacements);
    }
}
