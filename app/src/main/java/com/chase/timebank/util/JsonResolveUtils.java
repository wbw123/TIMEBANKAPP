package com.chase.timebank.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chase on 2018/4/19.
 */

public class JsonResolveUtils {
    /**
     * 解析json
     *
     * @param response
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T parseJsonToBean(String response, Class<T> cls) {
        Gson gson = new Gson();
        T t = gson.fromJson(response, cls);
        return t;
    }

    public static <T> List<T> parseJsonToList(String response, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            JsonArray arry = new JsonParser().parse(response).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /*public static <T> List<T> parseJsonToList(String response){
        Gson gson = new Gson();
        List<T> t = gson.fromJson(response, new TypeToken<List<T>>() {
        }.getType());
        return t;
    }*/

}
