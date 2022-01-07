package com.lorenzovngl.domina.utils;


import android.util.Log;

import java.util.List;

public class StringUtils {

    public static String listToString(List<Integer> list){
        String string = "";
        for (int i = 0; i < list.size(); i++){
            string = string.concat(Integer.toString(list.get(i)));
        }
        return string;
    }

    public static void printList(List<String> list){
        for (int i = 0; i < list.size(); i++){
            Log.i("STRING_ARRAY", "[" + i + "] " + list.get(i));
        }
    }

    public static String getTag() {
        String tag = "";
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (ste[i].getMethodName().equals("getTag")) {
                tag = "("+ste[i + 1].getFileName() + ":" + ste[i + 1].getLineNumber()+")";
            }
        }
        return tag;
    }
}
