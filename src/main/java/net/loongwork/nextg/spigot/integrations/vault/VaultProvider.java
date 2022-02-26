package net.loongwork.nextg.spigot.integrations.vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class VaultProvider {

    private final Economy economy;

    private final Chat chat;

    private final Permission permission;

    public VaultProvider(Economy economy, Chat chat, Permission permission) {
        this.economy = economy;
        this.chat = chat;
        this.permission = permission;
    }

    public VaultProvider() {
        this.economy = null;
        this.chat = null;
        this.permission = null;
    }

    public double getBalance(OfflinePlayer player) {
        if (economy == null) return 0d;
        return economy.getBalance(player);
    }

    public String getPlayerPrefix(OfflinePlayer player) {
        if (chat == null) return "";
        return chat.getPlayerPrefix("world", player);
    }

    public String getPlayerPrefix(Player player) {
        if (chat == null) return "";
        return chat.getPlayerPrefix("world", player);
    }
}
