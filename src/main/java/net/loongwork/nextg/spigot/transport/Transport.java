package net.loongwork.nextg.spigot.transport;

import lombok.val;
import net.loongwork.nextg.spigot.NextGSpigot;
import net.loongwork.nextg.spigot.transport.packets.NotifyPacket;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Transport {

    public static void sendNotify(String notify) {
        val service = HttpClient.getService();
        val call = service.sendNotify(new NotifyPacket(notify));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<NotifyPacket> call, @NotNull Response<NotifyPacket> response) {
            }

            @Override
            public void onFailure(@NotNull Call<NotifyPacket> call, @NotNull Throwable t) {
                NextGSpigot.instance().getLogger().warning("通知发送失败：" + call.request().body());
                t.printStackTrace();
            }
        });
    }
}
