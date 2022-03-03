package net.loongwork.nextg.spigot.whitelist;

import net.loongwork.nextg.spigot.NextGSpigot;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WhitelistRepo {

    private final File file;

    private final YamlConfiguration config;

    private final Map<UUID, WhitelistUser> whitelistUsers;

    public WhitelistRepo() {
        this.file = new File(NextGSpigot.instance().getDataFolder(), "whitelist.yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.whitelistUsers = new java.util.HashMap<>();
    }

    public void add(WhitelistUser user) {
        whitelistUsers.put(user.uuid(), user);
    }

    public void remove(WhitelistUser user) {
        remove(user.uuid());
    }

    public void remove(UUID uuid) {
        whitelistUsers.remove(uuid);
    }

    public WhitelistUser get(UUID uuid) {
        return whitelistUsers.get(uuid);
    }

    public void load() {
        List<String> uuids;
        if (config.contains("whitelist")) {
            uuids = new ArrayList<>(config.getStringList("whitelist"));
        } else {
            uuids = new ArrayList<>();
        }

        for (String uuid : uuids) {
            WhitelistUser user = new WhitelistUser(UUID.fromString(uuid), config.getString(uuid + ".username"), config.getString(uuid + ".type"), config.getLong(uuid + ".timestamp"));
            add(user);
        }
    }

    public void save() {
        for (UUID uuid : whitelistUsers.keySet()) {

            WhitelistUser user = whitelistUsers.get(uuid);

            config.set(uuid + ".uuid", user.uuid().toString());
            config.set(uuid + ".username", user.username());
            config.set(uuid + ".type", user.type());
            config.set(uuid + ".timestamp", user.timestamp());
        }

        List<UUID> uuids = new ArrayList<>(whitelistUsers.keySet());

        config.set("whitelist", uuids);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

