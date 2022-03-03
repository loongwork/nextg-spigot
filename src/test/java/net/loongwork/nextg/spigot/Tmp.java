package net.loongwork.nextg.spigot;

import com.google.gson.internal.LinkedTreeMap;
import lombok.val;
import net.loongwork.nextg.spigot.transport.HttpClient;
import net.loongwork.nextg.spigot.transport.packets.NotifyPacket;
import net.loongwork.nextg.spigot.transport.services.NextGService;
import net.loongwork.nextg.spigot.utils.CryptUtils;
import net.loongwork.nextg.spigot.utils.JSONUtils;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.LinkedHashMap;

public class Tmp {

    public static void main(String[] args) throws InterruptedException, IOException {
        val service = HttpClient.getService();
        val call = service.sendNotify(new NotifyPacket("server.start"));
        val response = call.execute();
        val body = response.body();
        System.out.println(body);
    }
}
