package com.ghostmc.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.ghostmc.android.app.NfcActivity;
import com.ghostmc.android.dataManager.DataSource;
import com.ghostmc.android.nfc.NfcUtils;
import com.ghostmc.android.ui.LogoSelector;
import com.ghostmc.android.utils.Share;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ReaderActivity extends NfcActivity {

    private final static String TAG = "ReaderActivity";

    private ShareActionProvider mShareActionProvider;

    private TextView mainText;
    private Button save;
    private Map<Integer, String> readed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        mainText = (TextView) findViewById(R.id.main_text);
        save = (Button) findViewById(R.id.save);
        save.setVisibility(View.GONE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                LogoSelector.launch(ReaderActivity.this, new LogoSelector.Handler() {
                    @Override
                    public void onLogoSelected(final String name) {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReaderActivity.this);

                        alertDialog.setTitle(R.string.save);
                        final View alertView = View.inflate(ReaderActivity.this, R.layout.dialog_save, null);
                        alertDialog
                                .setView(alertView)
                                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        saveCard(((EditText)alertView.findViewById(R.id.tag_name)).getText().toString(), name);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null).show();
                    }
                });

            }
        });
    }

    private void saveCard(String title, String logo) {
        DataSource.save(getApplicationContext(), readed, title, logo);
        Toast.makeText(getApplicationContext(), getString(R.string.saved_card), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReceiveTag(final Tag tag) {
        final MifareClassic mifareClassic = MifareClassic.get(tag);
        if (mifareClassic == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_mifare), Toast.LENGTH_LONG).show();
            return;
        }


        new AsyncTask<Void, Void, String>() {

            protected ProgressDialog progressDialog;

            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(ReaderActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                readed = new HashMap<Integer, String>();
                try {
                    readed = NfcUtils.read(getApplicationContext(), mifareClassic);
                } catch(IOException e) {
                    e.printStackTrace();
                }

                StringBuilder showText = new StringBuilder();

                for (Integer key : readed.keySet()) {
                    showText.append("Block ").append(key).append(": \n ");
                    showText.append(readed.get(key));
                    showText.append("\n \n");
                }

                return showText.toString();
            }

            @Override
            protected void onPostExecute(String text) {
                if (text.equals("")) {
                    text = getString(R.string.error);
                    save.setVisibility(View.GONE);
                } else {
                    save.setVisibility(View.VISIBLE);
                    setShareIntent(Share.getTextIntent(text));
                }
                mainText.setText(text);
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
