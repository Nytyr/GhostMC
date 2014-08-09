package com.ghostmc.android.ui;

import com.ghostmc.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagLogos {

    public static Map<String, Integer> logos = new HashMap<String, Integer>(){{
        put("bracelet", R.drawable.bracelet);
        put("card", R.drawable.card);
        put("mobile", R.drawable.mobile);
        put("tag", R.drawable.tag);
    }};

    public static int getDrawable(String logo) {
        if (logos.containsKey(logo)) {
            return logos.get(logo);
        }
        // Return default logo
        return logos.get("bracelet");
    }

    public static List<Integer> getLogos() {
        return new ArrayList<Integer>(logos.values());
    }
}
