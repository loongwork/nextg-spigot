package net.loongwork.nextg.spigot.transport;

import net.loongwork.nextg.spigot.transport.services.NextGService;
import net.loongwork.nextg.spigot.utils.JSONUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {

    private static Retrofit retrofit;

    private static NextGService service;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://c78b878d-4faf-4141-99d8-f6993c6672e8.mock.pstmn.io/api/")
                    .addConverterFactory(GsonConverterFactory.create(JSONUtils.getGson()))
                    .build();
        }
        return retrofit;
    }

    public static NextGService getService() {
        if (service == null) {
            service = getRetrofit().create(NextGService.class);
        }
        return service;
    }
}
