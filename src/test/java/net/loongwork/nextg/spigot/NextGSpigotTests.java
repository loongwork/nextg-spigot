package net.loongwork.nextg.spigot;

import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

public class NextGSpigotTests extends TestBase {

    @Test
    public void shouldFirePlayerJoinEvent() {
        server.addPlayer();

        server.getPluginManager().assertEventFired(PlayerJoinEvent.class);
    }
}
