package com.ghostmc.android;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ghostmc.android.app.NfcActivity;
import com.ghostmc.android.dataManager.DataSource;
import com.ghostmc.android.ui.TagSelector;

import java.util.List;

public class DeleteActivity extends NfcActivity {

    private final static String TAG = "DeleteActivity";

    private Spinner selector;

    private long currentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        selector = (Spinner) findViewById(R.id.selector);
        final Button delete = (Button) findViewById(R.id.delete);

        setSelector(true);

        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TagSelector.Item item = (TagSelector.Item) adapterView.getAdapter().getItem(i);
                currentId = item.id;
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

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(DeleteActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.continue_question)
                        .setMessage(R.string.delete_saved_card)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataSource.delete(getApplicationContext(), currentId);
                                Toast.makeText(getApplicationContext(), getString(R.string.deleted_card), Toast.LENGTH_SHORT).show();
                                setSelector(false);
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });
    }

    private void setSelector(boolean firstTime) {
        List<TagSelector.Item> selectorItems = TagSelector.get(getApplicationContext());

        if (selectorItems.isEmpty()) {
            if (firstTime) {
                Toast.makeText(getApplicationContext(), getString(R.string.firs_save_card), Toast.LENGTH_SHORT).show();
            }
            finish();
        }

        ArrayAdapter selectorContent = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                selectorItems
        );

        selector.setAdapter(selectorContent);
    }

    @Override
    public void onReceiveTag(final Tag tag) {
        final MifareClassic mifareClassic = MifareClassic.get(tag);
        if (mifareClassic == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_mifare), Toast.LENGTH_LONG).show();
        }

    }
}
