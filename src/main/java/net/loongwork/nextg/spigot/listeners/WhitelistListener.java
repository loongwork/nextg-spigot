package net.loongwork.nextg.spigot.listeners;

import com.google.common.util.concurrent.RateLimiter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.metadata.Metadata;
import net.loongwork.nextg.spigot.Constants;
import net.loongwork.nextg.spigot.models.User;
import net.loongwork.nextg.spigot.utils.I18NUtils;
import net.loongwork.nextg.spigot.utils.MaterialUtils;
import net.loongwork.nextg.spigot.utils.MessageUtils;
import net.loongwork.nextg.spigot.whitelist.Whitelist;
import org.bukkit.Bukkit;
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
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;

public class WhitelistListener implements Listener {

    private final RateLimiter rateLimiter = RateLimiter.create(1.0);

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        // 覆写原生 /whitelist
        System.out.println(event.getMessage());
        if (event.getMessage().matches("/whitelist.*")) {
            event.setMessage(event.getMessage().replaceFirst("/whitelist", "/lw wl"));
        }
    }

    @EventHandler
    public void onServerCommandEvent(ServerCommandEvent event) {
        // 覆写原生 /whitelist
        System.out.println(event.getCommand());
        if (event.getCommand().matches("whitelist.*")) {
            event.setCommand(event.getCommand().replaceFirst("whitelist", "lw wl"));
        }
    }

    @EventHandler
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        if (Whitelist.containsPlayer(event.getUniqueId())) {
            return;
        }

//        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, I18NUtils.getMessageComponent("whitelist.kick"));

        Metadata.provideForPlayer(event.getUniqueId()).put(Constants.PLAYER_IS_VISITOR, true);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (User.get(event.getPlayer()).isVisitor()) {
            Schedulers.async().runLater(() -> {
                MessageUtils.Title.show(event.getPlayer(), "whitelist.join-as-visitor", "whitelist.visitor-mode-desc");
                MessageUtils.ActionBar.showUntil(event.getPlayer(), "whitelist.join-as-visitor", () -> !User.get(event.getPlayer()).isVisitor());
            }, 60L);
        }
    }

    @EventHandler
    public void onPlayerQuitEven(PlayerQuitEvent event) {
        Whitelist.cleanPlayer(event.getPlayer().getUniqueId());
    }

    private boolean shouldBeCancel(Entity entity) {
        if (!entity.getType().equals(EntityType.PLAYER)) {
            return true;
        }

        Player player;
        if (entity instanceof Player) {
            player = (Player) entity;
        } else {
            player = Bukkit.getPlayer(entity.getUniqueId());
        }

        if (player == null) {
            return true;
        }

        if (User.get(player).isVisitor()) {
            if (rateLimiter.tryAcquire()) {
                player.sendMessage(I18NUtils.getMessageComponent("whitelist.interact-blocked"));
            }
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
        if (User.get(event.getPlayer()).isVisitor()) {
            String[] commandWhitelist = {"login", "register", "list", "help"};
            String command = event.getMessage();
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
