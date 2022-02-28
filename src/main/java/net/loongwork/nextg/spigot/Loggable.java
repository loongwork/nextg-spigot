package net.loongwork.nextg.spigot;

import java.util.logging.Logger;

public interface Loggable {
    default Logger logger() {
        return NextGSpigot.instance().getLogger();
    }
}
