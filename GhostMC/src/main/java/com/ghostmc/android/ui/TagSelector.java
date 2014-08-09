package com.ghostmc.android.ui;

import android.content.Context;

import com.ghostmc.android.dataManager.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TagSelector {

    public static List<Item> get(Context context) {
        List<Item> items = new ArrayList<Item>();
        JSONObject jsonObject = DataSource.getSavedJSON(context);
        Iterator<?> keys = jsonObject.keys();
        while (keys.hasNext()) {
            try {
                Item item = new Item();
                String key = (String) keys.next();
                item.id = Long.valueOf(key);
                item.name = jsonObject.getJSONObject(key).getString("name");

                // This if is for mantain compatibility for old versions
                if (jsonObject.getJSONObject(key).has("logo")) {
                    item.logo = jsonObject.getJSONObject(key).getString("logo");
                } else {
                    item.logo = "";
                }
                items.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return items;
    }

    public static class Item {
        public long id;
        public String name;
        public String logo;

        @Override
        public String toString() {
            return this.name;
        }
    }
}
