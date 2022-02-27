package net.loongwork.nextg.spigot.whitelist;

import java.util.UUID;

public record WhitelistUser(UUID uuid, String username, String type, long timestamp) {
}
