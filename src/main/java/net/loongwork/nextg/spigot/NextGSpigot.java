package net.loongwork.nextg.spigot;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.milkbowl.vault.economy.Economy;
import net.loongwork.nextg.spigot.commands.TemplateCommands;
import net.loongwork.nextg.spigot.integrations.vault.VaultProvider;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

@PluginMain
public class NextGSpigot extends JavaPlugin implements Listener {

    @Getter
    @Accessors(fluent = true)
    private static NextGSpigot instance;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private VaultProvider vault;

    private PaperCommandManager commandManager;

    public NextGSpigot() {
        instance = this;
    }

    public NextGSpigot(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        setupVaultIntegration();
        setupCommands();
    }

    private void setupVaultIntegration() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            vault = new VaultProvider(Objects.requireNonNull(getServer().getServicesManager().getRegistration(Economy.class)).getProvider());
        } else {
            vault = new VaultProvider();
        }
    }

    private void setupCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");

        loadCommandLocales(commandManager);

        commandManager.registerCommand(new TemplateCommands());
    }

    private void loadCommandLocales(PaperCommandManager commandManager) {
        try {
            saveResource("lang_en.yaml", true);
            saveResource("lang_zh.yaml", true);

            Locale locale;
            String langConfig = getConfig().getString("locale", "en");
            if (langConfig.contains("-")) {
                String[] langConfigSplit = langConfig.split("-");
                locale = new Locale(langConfigSplit[0], langConfigSplit[1]);
            } else {
                locale = new Locale(langConfig);
            }
            commandManager.getLocales().setDefaultLocale(locale);

            commandManager.getLocales().loadYamlLanguageFile("lang_en.yaml", Locale.ENGLISH);
            commandManager.getLocales().loadYamlLanguageFile("lang_zh.yaml", Locale.SIMPLIFIED_CHINESE);
            // 检测并使用客户端语言（如果可用）
            commandManager.usePerIssuerLocale(true);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Failed to load language config: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
