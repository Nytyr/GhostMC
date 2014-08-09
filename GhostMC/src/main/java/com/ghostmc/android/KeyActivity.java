package com.ghostmc.android;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ghostmc.android.app.NfcActivity;
import com.ghostmc.android.keyUtils.FileUtils;
import com.ghostmc.android.keyUtils.KeyDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class KeyActivity extends NfcActivity implements View.OnClickListener {

    private final static String TAG = "KeyActivity";
    private final static int REQUEST_CODE = 444;
    private Button selector, delete;
    private TextView jsonTitle, jsonContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);

        selector = (Button) findViewById(R.id.selector);
        selector.setOnClickListener(this);
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);

        jsonTitle = (TextView) findViewById(R.id.json_title);
        jsonContent = (TextView) findViewById(R.id.json_content);

        setKeys();
    }

    @Override
    public void onReceiveTag(final Tag tag) {
        final MifareClassic mifareClassic = MifareClassic.get(tag);
        if (mifareClassic == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_mifare), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selector:
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("text/plain");
                startActivityForResult(fileIntent, REQUEST_CODE);
                break;
            case R.id.delete:
                new AlertDialog.Builder(KeyActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.continue_question)
                        .setMessage(R.string.delete_keys_question)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                KeyDataSource.saveKeys(getApplicationContext(), new JSONArray());
                                Toast.makeText(getApplicationContext(), getString(R.string.deleted_keys), Toast.LENGTH_SHORT).show();
                                jsonTitle.setText(R.string.no_keys);
                                jsonContent.setText("");
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                break;
        }
    }

    protected void setKeys() {
        JSONArray keys = KeyDataSource.getKeys(getApplicationContext());
        if (keys.length() > 0) {
            jsonTitle.setText(R.string.saved_keys);
            jsonContent.setText(getBeautifulJSON(keys));
        }
    }

    protected String getBeautifulJSON(JSONArray json) {
        StringBuilder beautifulJSON = new StringBuilder();
        try {
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                beautifulJSON.append(jsonObject.getString("key"));
                beautifulJSON.append(" > ");
                beautifulJSON.append(jsonObject.getString("value"));
                beautifulJSON.append(" \n ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            jsonTitle.length();
        }
        return beautifulJSON.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            // Open File
            InputStream is;
            try {
                is = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                return;
            }

            // Read file
            String json;
            try {
                json = FileUtils.readFile(is);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.error_reading_file), Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "JSON > "+ json);

            // Validate json
            JSONArray jsonArray;

            try {
                jsonArray = new JSONArray(json);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.error_parsing_json), Toast.LENGTH_SHORT).show();
                return;
            }

            boolean validJson = false;
            try {
                validJson = FileUtils.validateJSONKeys(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.error_parsing_json), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validJson) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_validating_json), Toast.LENGTH_SHORT).show();
                return;
            }

            KeyDataSource.saveKeys(getApplicationContext(), jsonArray);
            setKeys();
        }
    }
}
