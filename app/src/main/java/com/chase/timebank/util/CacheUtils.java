package com.chase.timebank.util;

import android.content.Context;

public class CacheUtils {
    /**
     * 写缓存
     * @param url   URL对象
     * @param json  缓存的数据
     * @param context   上下文
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
        return json;//返回的是json数据
    }
}