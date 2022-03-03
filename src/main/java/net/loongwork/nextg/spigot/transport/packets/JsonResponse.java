package net.loongwork.nextg.spigot.transport.packets;

import lombok.Getter;

public class JsonResponse {

    @Getter
    private int status;

    @Getter
    private String message;

    @Getter
    private Object data;

    public JsonResponse(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
