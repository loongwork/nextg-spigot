package net.loongwork.nextg.spigot.listeners;

import me.lucko.helper.Schedulers;
import me.lucko.helper.metadata.Metadata;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.loongwork.nextg.spigot.Constants;
import net.loongwork.nextg.spigot.utils.I18NUtils;
import net.loongwork.nextg.spigot.utils.MaterialUtils;
import net.loongwork.nextg.spigot.utils.MessageUtils;
import net.loongwork.nextg.spigot.whitelist.Whitelist;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class WhitelistListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        if (Whitelist.containsPlayer(event.getName())) {
            return;
        }

//        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, I18NUtils.getMessageComponent("whitelist.kick"));

        Metadata.provideForPlayer(event.getUniqueId()).put(Constants.PLAYER_IS_VISITOR, true);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (Metadata.provideForPlayer(event.getPlayer()).getOrDefault(Constants.PLAYER_IS_VISITOR, false)) {
            Schedulers.async().runLater(() -> {
                MessageUtils.Title.show(event.getPlayer(), "whitelist.join-as-visitor", "whitelist.visitor-mode-desc");
                MessageUtils.ActionBar.showUntil(event.getPlayer(), "whitelist.join-as-visitor", () -> !Metadata.provideForPlayer(event.getPlayer()).getOrDefault(Constants.PLAYER_IS_VISITOR, false));
            }, 60L);
        }
    }

    private boolean shouldBeCancel(Entity entity) {
        if (!entity.getType().equals(EntityType.PLAYER)) {
            return true;
        }

        final Player player;
        if (entity instanceof Player) {
            player = (Player) entity;
        } else {
            player = Bukkit.getPlayer(entity.getUniqueId());
        }

        if (Metadata.provideForPlayer(Objects.requireNonNull(player)).getOrDefault(Constants.PLAYER_IS_VISITOR, false)) {
            player.sendMessage(I18NUtils.getMessageComponent("whitelist.interact-blocked"));
            return true;
        } else {
            return false;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (shouldBeCancel(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBreakBlock(final BlockBreakEvent event) {
        if (shouldBeCancel(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {
        if (shouldBeCancel(event.getDamager()) || shouldBeCancel(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (MaterialUtils.isSign(event.getClickedBlock().getType()) && shouldBeCancel(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerUseCommand(final PlayerCommandPreprocessEvent event) {
        if (Metadata.provideForPlayer(event.getPlayer()).getOrDefault(Constants.PLAYER_IS_VISITOR, false)) {
            final String[] commandWhitelist = {"login", "register", "list", "help"};
            final String command = event.getMessage();
            boolean allowExecute = false;
            for (String whiteCommand : commandWhitelist) {
                if (command.startsWith("/" + whiteCommand)) {
                    allowExecute = true;
                    break;
                }
            }
            if (!allowExecute) {
                event.getPlayer().sendMessage(I18NUtils.getMessageComponent("whitelist.interact-blocked"));
                event.setCancelled(true);
            }
        }
    }
}
