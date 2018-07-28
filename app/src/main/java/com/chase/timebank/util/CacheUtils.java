package com.chase.timebank.util;

import android.content.Context;

public class CacheUtils {
    /**
     * 写缓存
     * @param url
     * @param json
     * @param context
     */
    public static void setCache(String url, String json, Context context){
        PrefUtils.putString(url,json,context);
    }

    /**
     * 读缓存
     * @param url
     * @param context
     */
    public static String getCache(String url, Context context){
        String json = PrefUtils.getString(url, null, context);
        return json;
    }
}