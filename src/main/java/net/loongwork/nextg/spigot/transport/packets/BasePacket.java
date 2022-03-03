package net.loongwork.nextg.spigot.transport.packets;

import lombok.Getter;
import net.loongwork.nextg.spigot.utils.JSONUtils;

public class BasePacket {

    @Getter
    private final String packetName;

    @Getter
    private final String content;

    public BasePacket(String packetName, String content) {
        this.packetName = packetName;
        this.content = content;
    }

    public int getPacketLength() {
        return this.serialize().length();
    }

    public String serialize() {
        return JSONUtils.encode(this);
    }

    public static <T extends BasePacket> T deserialize(String json, Class<T> clazz) {
        return JSONUtils.decode(json, clazz);
    }
}
