package com.chase.timebank.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by chase on 2018/4/19.
 */

public class ToastUtils {
    public static void ToastLong(Context context,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
    public static void ToastShort(Context context,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
