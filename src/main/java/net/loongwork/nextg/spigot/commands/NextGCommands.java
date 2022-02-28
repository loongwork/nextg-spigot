package net.loongwork.nextg.spigot.commands;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import lombok.val;
import net.loongwork.nextg.spigot.Constants;
import net.loongwork.nextg.spigot.NextGSpigot;
import net.loongwork.nextg.spigot.models.User;
import net.loongwork.nextg.spigot.whitelist.Whitelist;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("%command.prefix")
public class NextGCommands extends BaseCommand {

    @HelpCommand
    @Subcommand("help")
    public void showHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("info|version")
    @Description("{@@commands.descriptions.info}")
    public void info() {
        val pluginDescription = NextGSpigot.instance().getDescription();
        reply("info",
                "{plugin_name}", pluginDescription.getName(),
                "{plugin_version}", pluginDescription.getVersion(),
                "{plugin_compatibility}", pluginDescription.getAPIVersion(),
                "{plugin_protocol}", "NextG",
                "{credit}", "本插件为 LoongWork 定制开发"
        );
    }

    @Subcommand("whitelist|wl")
    @Description("xxx")
    @CommandPermission(Constants.PERMISSION_ADMIN)
    public class WhitelistCommand extends BaseCommand {

        @Subcommand("add")
        @Description("{@@commands.descriptions.whitelist.add}")
        @CommandCompletion("@players")
        public void add(CommandSender sender, @Flags("other") Player player) {
            Whitelist.addPlayer(player);
            reply("whitelist.add", "{player}", player.getName());
        }

        @Subcommand("remove")
        @Description("{@@commands.descriptions.whitelist.remove}")
        @CommandCompletion("@players")
        public void remove(CommandSender sender, @Flags("other") Player player) {
            Whitelist.removePlayer(player);
            reply("whitelist.remove", "{player}", player.getName());
        }

        @Subcommand("list")
        @Description("{@@commands.descriptions.whitelist.list}")
        @Private
        public void list(CommandSender sender) {
            sender.sendMessage(User.get((Player) sender).username());
            reply("whitelist.list");
        }

        @Subcommand("reload")
        @Description("{@@commands.descriptions.whitelist.reload}")
        public void reload(CommandSender sender) {
            Whitelist.reload();
            reply("whitelist.reload");
        }
    }
}
