package com.ghostmc.android.utils;

import android.content.Intent;

public class Share {
    public static Intent getTextIntent(String textToShare) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        sendIntent.setType("text/plain");
        return sendIntent;
    }
}
