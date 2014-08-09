package com.ghostmc.android.keyUtils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class FileUtils {

    public static String readFile(InputStream inputStream) throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        int n;
        while ((n = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, n);
        }
        inputStream.close();

        return writer.toString();
    }

    public static boolean validateJSONKeys(JSONArray json) throws JSONException {
        if (json.length() != 32) {
            return false;
        }
        int sector = 0;
        for (int i = 0; i < json.length(); i = i+2) {
            JSONObject jsonObject = json.getJSONObject(i);

            if (!jsonObject.has("key") || !jsonObject.has("value")) {
                return false;
            }

            if (!jsonObject.getString("key").equals(sector+":A")) {
                return false;
            }

            jsonObject = json.getJSONObject(i+1);
            if (!jsonObject.getString("key").equals(sector+":B")) {
                return false;
            }

            sector++;
        }

        return true;
    }
}
