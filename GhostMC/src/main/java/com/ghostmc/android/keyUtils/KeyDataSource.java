package com.ghostmc.android.keyUtils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

public class KeyDataSource {

    protected final static String SHARED_NAME = "ghostKeySource";
    protected final static String FIELD = "ghostKeyData";

    public static JSONArray getKeys(Context context){
        final SharedPreferences prefs = getPreferences(context);
        try {
            return new JSONArray(prefs.getString(FIELD, "[]"));
        } catch(JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static void saveKeys(Context context, JSONArray keys){
        final SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FIELD, keys.toString());
        editor.commit();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(SHARED_NAME,
                Context.MODE_PRIVATE);
    }
}
