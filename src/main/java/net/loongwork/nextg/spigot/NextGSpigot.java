package net.loongwork.nextg.spigot;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.*;
import lombok.experimental.Accessors;
import net.loongwork.nextg.spigot.commands.CommonCommands;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.loongwork.nextg.spigot.commands.TemplateCommands;
import net.loongwork.nextg.spigot.integrations.vault.VaultProvider;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    public NextGSpigot(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
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
            val services = getServer().getServicesManager();
            vault = new VaultProvider(
                    Objects.requireNonNull(services.getRegistration(Economy.class)).getProvider(),
                    Objects.requireNonNull(services.getRegistration(Chat.class)).getProvider(),
                    Objects.requireNonNull(services.getRegistration(Permission.class)).getProvider()
            );
        } else {
            vault = new VaultProvider();
        }
    }

    @SneakyThrows
    private void setupCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");

        loadCommandLocales(commandManager);

        commandManager.registerCommand(new TemplateCommands());
        commandManager.registerCommand(new CommonCommands());
    }

    private void loadCommandLocales(PaperCommandManager commandManager) {
        try {
            saveResource("lang_en.yaml", true);
            saveResource("lang_zh.yaml", true);

            Locale locale;
            String langConfig = getConfig().getString("lang", "en");
            if (langConfig.contains("-")) {
                String[] langConfigSplit = langConfig.split("-");
                locale = new Locale(langConfigSplit[0], langConfigSplit[1]);
            } else {
                locale = new Locale(langConfig);
            }
            commandManager.getLocales().setDefaultLocale(locale);
            getLogger().info("Using locale: " + locale.getDisplayName());

            // 加载所有语言文件
            File langDir = new File(getDataFolder().getPath());
            File[] langFiles = langDir.listFiles();
            if (langFiles != null) {
                for (File langFile : langFiles) {
                    // if file name match pattern
                    if (langFile.getName().matches("lang_[a-zA-Z-]+\\.yaml")) {
                        var langFileLocaleString = langFile.getName().split("_")[1].split("\\.")[0];
                        Locale langFileLocale;
                        if (langFileLocaleString.contains("-")) {
                            String[] langConfigSplit = langFileLocaleString.split("-");
                            langFileLocale = new Locale(langConfigSplit[0], langConfigSplit[1]);
                        } else {
                            langFileLocale = new Locale(langFileLocaleString);
                        }
                        commandManager.getLocales().loadYamlLanguageFile(langFile, langFileLocale);
                        getLogger().info("Loaded language file: " + langFile.getName() + " for locale: " + langFileLocale.getDisplayName());
                    }
                }
            }

            // 检测并使用客户端语言（如果可用）
            commandManager.usePerIssuerLocale(true);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Failed to load language config: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
