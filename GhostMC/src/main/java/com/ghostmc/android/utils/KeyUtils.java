package com.ghostmc.android.utils;

import android.content.Context;
import android.util.Log;

import com.ghostmc.android.R;
import com.ghostmc.android.keyUtils.KeyDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class KeyUtils {

    private Map<String, byte[]> keys = new HashMap<String, byte[]>();

    public byte[] getKey(String sector) {
        return keys.get(sector);
    }

    public void readKeysFromJSON(Context context) {

        JSONArray keys = KeyDataSource.getKeys(context);
        if (keys.length() > 0) {
            jsonToObject(keys);
            return;
        }

        // No saved keys. Read the default ones...
        Log.d("KeyUtils", "No saved keys... Using FFFFFFFFFFFF");
        InputStream is = context.getResources().openRawResource(R.raw.keys);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            jsonToObject(jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void jsonToObject(JSONArray jsonArray) {
        try {
            JSONObject jsonObject;

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                keys.put(jsonObject.getString("key"), Binary.stringToBytes(jsonObject.getString("value")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
