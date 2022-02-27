package net.loongwork.nextg.spigot.whitelist;

import net.loongwork.nextg.spigot.NextGSpigot;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WhitelistRepo {

    private final File file;

    private final YamlConfiguration config;

    private final Map<UUID, WhitelistUser> players;

    public WhitelistRepo() {
        this.file = new File(NextGSpigot.instance().getDataFolder(), "whitelist.yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.players = new java.util.HashMap<>();
        load();
    }

    public void add(WhitelistUser user) {
        players.put(user.uuid(), user);
    }

    public void remove(WhitelistUser user) {
        players.remove(user.uuid());
    }

    public void remove(UUID uuid) {
        players.remove(uuid);
    }

    public WhitelistUser get(UUID uuid) {
        return players.get(uuid);
    }

    public void load() {
        List<String> uuids = new ArrayList<>();

        if (config.contains("uuids")) {
            uuids.addAll(config.getStringList("uuids"));
        }

        for (String uuidString : uuids) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                String username = config.getString(uuidString + ".username");
                String type = config.getString(uuidString + ".type");
                long timestamp = config.getLong(uuidString + ".timestamp");

                add(new WhitelistUser(uuid, username, type, timestamp));
            } catch (Exception ignored) {
            }
        }
    }

    public void save() {
        for (UUID uuid : players.keySet()) {

            WhitelistUser user = players.get(uuid);

            config.set(uuid + ".uuid", uuid.toString());
            config.set(uuid + ".username", user.username());
            config.set(uuid + ".type", user.type());
            config.set(uuid + ".timestamp", user.timestamp());
        }

        List<String> uuids = new ArrayList<>();

        for (UUID uuid : players.keySet()) {
            uuids.add(uuid.toString());
        }

        config.set("uuids", uuids);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

