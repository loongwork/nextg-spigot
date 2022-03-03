package net.loongwork.nextg.spigot.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONUtils {

    private static final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    public static Gson getGson() {
        return gson;
    }

    public static String encode(Object object) {
        return gson.toJson(object);
    }

    public static <T> T decode(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
