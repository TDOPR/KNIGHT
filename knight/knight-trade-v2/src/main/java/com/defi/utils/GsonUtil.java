package com.defi.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GsonUtil {

    static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    /**
     * <pre>
     * JSON字符串转换为List数组, 提供两种方式(主要解决调用的容易程度)
     * 1. TypeToken<List<T>> token 参数转换
     * 2. Class<T> cls 方式转换
     *
     * @param json
     * @return List<T>
     *
     * <pre>
     */
    public static <T> List<T> convertList(String json, TypeToken<List<T>> token) {
        if (StringUtils.isEmpty(json)) {
            return new ArrayList<T>();
        }
        return gson.fromJson(json, token.getType());
    }

    /**
     * <pre>
     * Json格式转换, 由JSON字符串转化到制定类型T
     *
     * @param json
     * @param cls
     * @return T
     *
     * <pre>
     */
    public static <T> T convertObj(String json, Class<T> cls) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, cls);
    }

    /**
     * <pre>
     * java对象转化JSON
     *
     * @return String
     *
     * <pre>
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        return gson.toJson(obj);
    }

    public static String getJsonObjectAsString(JsonObject jsonObject, String name) {
        if (jsonObject == null || StringUtils.isEmpty(name)) {
            return null;
        }
        JsonElement jsonElement = jsonObject.get(name);
        return (jsonElement == null) ? null : jsonElement.getAsString();
    }

    public static JsonObject getJsonObjectChild(JsonObject jsonObject, String name) {
        if (jsonObject == null || StringUtils.isEmpty(name)) {
            return null;
        }
        JsonElement jsonElement = jsonObject.get(name);
        return (jsonElement == null) ? null : jsonElement.getAsJsonObject();
    }

    public static boolean getJsonObjectAsBoolean(JsonObject jsonObject, String name) {
        if (jsonObject == null || StringUtils.isEmpty(name)) {
            return false;
        }
        JsonElement jsonElement = jsonObject.get(name);
        return (jsonElement == null) ? false : jsonElement.getAsBoolean();
    }
}
