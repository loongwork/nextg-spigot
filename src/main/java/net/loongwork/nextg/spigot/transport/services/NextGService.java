package net.loongwork.nextg.spigot.transport.services;

import net.loongwork.nextg.spigot.transport.packets.JsonResponse;
import net.loongwork.nextg.spigot.transport.packets.NotifyPacket;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NextGService {

    @GET("users/{user}")
    Call<JsonResponse> getUser(@Path("user") String user);

    @POST("spigot/webhook")
    Call<NotifyPacket> sendNotify(@Body NotifyPacket notifyPacket);
}
