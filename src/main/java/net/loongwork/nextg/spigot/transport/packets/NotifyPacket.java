package net.loongwork.nextg.spigot.transport.packets;

public class NotifyPacket extends BasePacket {
    public NotifyPacket(String notice) {
        super(notice, null);
    }
}
