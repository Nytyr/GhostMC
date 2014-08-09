package com.ghostmc.android.nfc;

import android.content.Context;
import android.nfc.tech.MifareClassic;
import android.util.Log;

import com.ghostmc.android.utils.Binary;
import com.ghostmc.android.utils.KeyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class NfcUtils {

    private final static String TAG = "NfcUtils";

    public static Map<Integer, String> read(Context context, MifareClassic mMfc) throws IOException {
        Map<Integer, String> readedBlocks = new LinkedHashMap<Integer, String>();

        mMfc.connect();
        Log.d(TAG, "Connected to tag");
        int sectors = mMfc.getSectorCount();
        Log.d(TAG, "Sector count > "+sectors);

        KeyUtils keyUtils = new KeyUtils();
        keyUtils.readKeysFromJSON(context);

        boolean auth = false;

        for (int sectorIndex = 0; sectorIndex < sectors; sectorIndex++) {

            Log.d(TAG, "Authenticating to sector "+sectorIndex);

            // Try to auth with key A
            auth = mMfc.authenticateSectorWithKeyA(sectorIndex, keyUtils.getKey(sectorIndex+":A"));

            if (!auth) {
                // Try to auth with key B
                Log.d(TAG, "Authentication with key A failed. Let's try key B...");
                auth = mMfc.authenticateSectorWithKeyA(sectorIndex, keyUtils.getKey(sectorIndex+":B"));
            }

            Log.d(TAG, "Authentication? > "+auth);
            if (auth) {

                int blockCount = mMfc.getBlockCountInSector(sectorIndex);
                Log.d(TAG, "Block count > " + blockCount);
                int bIndex = 0;
                for(int i = 0; i < blockCount; i++) {
                    bIndex = mMfc.sectorToBlock(sectorIndex);
                    String content = Binary.bytesToString(mMfc.readBlock(bIndex+i));
                    Log.d(TAG, "Block > "+(bIndex+i)+" Content > "+content);
                    if (i == 3) {
                        content = Binary.bytesToString(keyUtils.getKey(sectorIndex+":A"))
                                + content.substring(12, 20)
                                + Binary.bytesToString(keyUtils.getKey(sectorIndex+":B"));
                    }
                    readedBlocks.put(bIndex+i, content);
                }

                Log.d(TAG, "End reading blocks...");
            }
        }

        Log.d(TAG, "Closing tag");
        mMfc.close();

        return readedBlocks;
    }

    public static ArrayList<String> write(Context context, MifareClassic mMfc, Map<Integer, String> data, final boolean writeBlock0) throws IOException {
        ArrayList<String> blocksFailed = new ArrayList<String>();
        mMfc.connect();
        Log.d(TAG, "Connected to tag");
        int sectors = mMfc.getSectorCount();
        Log.d(TAG, "Sector count > "+sectors);

        KeyUtils keyUtils = new KeyUtils();
        keyUtils.readKeysFromJSON(context);

        boolean auth = false;

        for (int sectorIndex = 0; sectorIndex < sectors; sectorIndex++) {

            Log.d(TAG, "Authenticating to sector "+sectorIndex);

            // Try to auth with key B
            auth = mMfc.authenticateSectorWithKeyB(sectorIndex, keyUtils.getKey(sectorIndex+":B"));

            if (!auth) {
                // Try to auth with key A
                Log.d(TAG, "Authentication with key B failed. Let's try key A...");
                auth = mMfc.authenticateSectorWithKeyA(sectorIndex, keyUtils.getKey(sectorIndex+":A"));
            }

            Log.d(TAG, "Authentication? > "+auth);
            if (auth) {

                int blockCount = mMfc.getBlockCountInSector(sectorIndex);
                Log.d(TAG, "Block count > " + blockCount);
                int bIndex = 0;
                for(int i = 0; i < blockCount; i++) {
                    bIndex = mMfc.sectorToBlock(sectorIndex);

                    boolean canWrite = true;
                    if (!writeBlock0 && (bIndex+i) == 0) {
                        canWrite = false;
                    }
                    if (canWrite) {
                        Log.d(TAG, "Lets write the block > " + (bIndex + i) + " With > " + data.get(bIndex + i));
                        try {
                            mMfc.writeBlock(bIndex + i, Binary.stringToBytes(data.get(bIndex + i)));
                        } catch (IOException e) {
                            e.printStackTrace();
                            blocksFailed.add(String.valueOf(bIndex + i));
                        }
                    }
                }

                Log.d(TAG, "End writing blocks...");
            }
        }

        Log.d(TAG, "Closing tag");
        mMfc.close();
        return blocksFailed;
    }

}
