package net.loongwork.nextg.spigot.whitelist;

import net.loongwork.nextg.spigot.NextGSpigot;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WhitelistRepo {

    private final File file;

    private final YamlConfiguration config;

    private final Map<String, WhitelistUser> whitelistUsers;

    public WhitelistRepo() {
        this.file = new File(NextGSpigot.instance().getDataFolder(), "whitelist.yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.whitelistUsers = new java.util.HashMap<>();
        load();
    }

    public void add(WhitelistUser user) {
        whitelistUsers.put(user.username(), user);
    }

    public void remove(WhitelistUser user) {
        remove(user.username());
    }

    public void remove(String username) {
        whitelistUsers.remove(username);
    }

    public WhitelistUser get(String username) {
        return whitelistUsers.get(username);
    }

    public void load() {
        List<String> usernames;
        if (config.contains("whitelist")) {
            usernames = new ArrayList<>(config.getStringList("whitelist"));
        } else {
            usernames = new ArrayList<>();
        }

        for (String username : usernames) {
            WhitelistUser user = new WhitelistUser(UUID.fromString(Objects.requireNonNull(config.getString(username + ".uuid"))), config.getString(username + ".username"), config.getString(username + ".type"), config.getLong(username + ".timestamp"));
            add(user);
        }
    }

    public void save() {
        for (String username : whitelistUsers.keySet()) {

            WhitelistUser user = whitelistUsers.get(username);

            config.set(username + ".uuid", user.uuid().toString());
            config.set(username + ".username", user.username());
            config.set(username + ".type", user.type());
            config.set(username + ".timestamp", user.timestamp());
        }

        List<String> usernames = new ArrayList<>(whitelistUsers.keySet());

        config.set("whitelist", usernames);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

