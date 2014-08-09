package com.ghostmc.android.app;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public abstract class NfcActivity extends FragmentActivity {

    //NFC parameters
    private NfcAdapter nfcAdapter;
    private PendingIntent pending;
    private String[][] technologies;
    private IntentFilter[] filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNFC();
    }

    private void initNFC(){
        nfcAdapter= NfcAdapter.getDefaultAdapter(this);
        pending= PendingIntent.getActivity(
                this,
                0,
                new Intent(
                        this,
                        getClass()
                ).addFlags(
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                ),
                0
        );
        technologies = new String[][] {
                new String[] {IsoDep.class.getName()},
                new String[] {MifareClassic.class.getName()},
                new String[] {MifareUltralight.class.getName()},
                new String[] {Ndef.class.getName()},
                new String[] {NfcA.class.getName()},
                new String[] {NfcB.class.getName()},
                new String[] {NfcF.class.getName()},
                new String[] {NfcV.class.getName()}
        };
        IntentFilter intentFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        try {
            intentFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        filters = new IntentFilter[] {
                intentFilter
        };
    }

    @Override
    public void onNewIntent(Intent intent){
        String action = intent.getAction();
        if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            onReceiveTag(tag);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Enable NFC
        nfcAdapter.enableForegroundDispatch(
                this, pending,
                filters, technologies
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        //Disable NFC
        nfcAdapter.disableForegroundDispatch(this);
    }

    public abstract void onReceiveTag(Tag tag);
}
