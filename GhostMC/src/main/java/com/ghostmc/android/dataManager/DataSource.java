package com.ghostmc.android.dataManager;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataSource {

    public static LinkedHashMap<Integer, String> get(Context context, long id) {
        try {
            return jsonToMap((JSONArray) getSavedJSON(context).getJSONObject(String.valueOf(id)).get("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new LinkedHashMap<Integer, String>();
    }

    public static void delete(Context context, long id) {
        JSONObject jsonObject = getSavedJSON(context);
        jsonObject.remove(String.valueOf(id));
        try {
            saveJSON(context, jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long save(Context context, Map<Integer, String> data, String name, String logo) {
        JSONObject jsonObject = getSavedJSON(context);

        // Get last ID. TODO This is a fast workaround
        long id = 1;
        if (jsonObject.length() > 0) {
            Iterator<?> keys = jsonObject.keys();
            int[] ids = new int[jsonObject.length()];
            int i = 0;
            while (keys.hasNext()) {
                ids[i] = Integer.valueOf((String)keys.next());
                i++;
            }

            Arrays.sort(ids);

            id = ids[ids.length-1]+1;
        }

        try {
            JSONObject finalJSON = new JSONObject();
            finalJSON.put("name", name);
            finalJSON.put("logo", logo);
            finalJSON.put("data", mapToJSON(data));
            jsonObject.put(String.valueOf(id), finalJSON);
            saveJSON(context, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return id;
    }

    public static JSONObject getSavedJSON(Context context){
        JSONObject data = new JSONObject();
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(getFile(context)), "UTF-8"));

            data = new JSONObject(in.readLine());
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void saveJSON(Context context, JSONObject json) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getFile(context)), "UTF-8"));
        out.write(json.toString());
        out.close();
    }

    private static String getFolder(Context context) {
        String path = new File(context.getCacheDir(), "") + "tags/";

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    private static String getFile(Context context) {
        String path = getFolder(context)+"data";

        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    private static LinkedHashMap<Integer, String> jsonToMap(JSONArray jsonArray) {
        LinkedHashMap<Integer, String> linkedHashMap = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                linkedHashMap.put(jsonObject.getInt("key"), jsonObject.getString("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return linkedHashMap;
    }

    private static JSONArray mapToJSON(Map<Integer, String> data) {
        JSONArray jsonArray = new JSONArray();

        for (Integer key : data.keySet()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("key", key);
                jsonObject.put("value", data.get(key));
                jsonArray.put(jsonObject);
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray;
    }
}
