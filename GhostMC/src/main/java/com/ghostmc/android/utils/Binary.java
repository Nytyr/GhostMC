package com.ghostmc.android.utils;


public class Binary{
    private static final String TAG = "Binary";

    public static final String HEX_TABLE = "0123456789abcdef";

    public static String bytesToString(byte[] raw){
        int len=raw.length;
        byte[] hex = new byte[2 * len];
        int index = 0;
        int pos = 0;

        for (byte b : raw) {
            if (pos >= len)
                break;

            pos++;
            int v = b & 0xFF;
            hex[index++] = (byte) HEX_TABLE.charAt(v >>> 4);
            hex[index++] = (byte) HEX_TABLE.charAt(v & 0xF);
        }

        return new String(hex);
    }

    public static byte[] stringToBytes(String hex){
        String lowerHex = hex.toLowerCase();

        byte[] raw = new byte[lowerHex.length()/2];

        for(int i=0;i<lowerHex.length();i+=2){
            raw[i/2] = (byte) (HEX_TABLE.indexOf(lowerHex.charAt(i))*16);
            raw[i/2] += HEX_TABLE.indexOf(lowerHex.charAt(i+1));
        }

        return raw;
    }


}