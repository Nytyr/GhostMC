package com.ghostmc.android;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ghostmc.android.app.NfcActivity;
import com.ghostmc.android.dataManager.DataSource;
import com.ghostmc.android.nfc.NfcUtils;
import com.ghostmc.android.ui.TagSelector;
import com.ghostmc.android.utils.Share;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class WritterActivity extends NfcActivity {

    private final static String TAG = "WritterActivity";

    private ShareActionProvider mShareActionProvider;

    private TextView mainText;
    private CheckBox write0;

    private boolean canWrite = false;
    private ArrayList<String> failedBlocks;

    private LinkedHashMap<Integer, String> currentData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writter);

        mainText = (TextView) findViewById(R.id.main_text);

        final Spinner selector = (Spinner) findViewById(R.id.selector);
        final Button write = (Button) findViewById(R.id.write);
        write0 = (CheckBox) findViewById(R.id.write0);

        List<TagSelector.Item> selectorItems = TagSelector.get(getApplicationContext());

        if (selectorItems.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.firs_save_card), Toast.LENGTH_SHORT).show();
            finish();
        }

        ArrayAdapter selectorContent = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                selectorItems
        );

        selector.setAdapter(selectorContent);

        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TagSelector.Item item = (TagSelector.Item) adapterView.getAdapter().getItem(i);
                currentData = DataSource.get(getApplicationContext(), item.id);
                showSelectedContent();
                canWrite = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selector.setSelection(extras.getInt("selectedTag"));
        }

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WritterActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.continue_question)
                        .setMessage(R.string.confirm_write)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                canWrite = true;
                                mainText.setText(R.string.close_tag);
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });
    }

    private void showSelectedContent() {
        StringBuilder showText = new StringBuilder();

        for (Integer key : currentData.keySet()) {
            showText.append("Block ").append(key).append(": \n ");
            showText.append(currentData.get(key));
            showText.append("\n \n");
        }

        setShareIntent(Share.getTextIntent(showText.toString()));
        mainText.setText(showText.toString());
    }

    @Override
    public void onReceiveTag(final Tag tag) {
        final MifareClassic mifareClassic = MifareClassic.get(tag);
        if (mifareClassic == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_mifare), Toast.LENGTH_LONG).show();
        }

        if (!canWrite) {
            return;
        }

        // Let' write...
        new AsyncTask<Void, Void, Boolean>() {

            protected ProgressDialog progressDialog;

            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(WritterActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    failedBlocks = NfcUtils.write(getApplicationContext(), mifareClassic, currentData, write0.isChecked());
                    return true;
                } catch(IOException e) {
                    e.printStackTrace();

                    return false;
                }

            }

            @Override
            protected void onPostExecute(Boolean writed) {
                if (writed) {

                    mainText.setText(R.string.write_success);

                    if (failedBlocks.size() > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(getString(R.string.write_success)).append(" \n ");
                        stringBuilder.append(getString(R.string.blocks_failed)).append(" \n ");
                        for (String block : failedBlocks) {
                            stringBuilder.append(block);
                            stringBuilder.append(" \n ");
                        }
                        mainText.setText(stringBuilder.toString());
                    }

                } else {
                    mainText.setText(R.string.error_writing);
                }
                progressDialog.dismiss();
            }
        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.share_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem menuShare = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) menuShare.getActionProvider();

        // Return true to display menu
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

}
