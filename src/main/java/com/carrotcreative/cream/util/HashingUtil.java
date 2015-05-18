package com.carrotcreative.cream.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.support.annotation.NonNull;

public class HashingUtil
{
    private static final int CACHE_SIZE = 50;
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static LruCache<String, String> mCache = new LruCache<>(CACHE_SIZE);

    private HashingUtil() {}

    /**
     *
     * @param secret The string that needs to be hashed.
     * @return The hashed value.
     */
    public static String getHash(@NonNull String secret)
    {
        String hashedValue = mCache.get(secret);
        if(hashedValue == null)
        {
            hashedValue = generateHash(secret);
            mCache.put(secret, hashedValue);
        }

        return hashedValue;
    }

    private static String generateHash(String secret)
    {
        String hashedString = null;
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(secret.getBytes("UTF-8"));
            hashedString = bytesToHex(messageDigest.digest());
        }
        catch (NoSuchAlgorithmException e) { /* Do nothing */ }
        catch (UnsupportedEncodingException e) { /* Do nothing */ }

        if(hashedString == null)
        {
            hashedString = secret.replaceAll("\\s+", "_");
        }
        return hashedString;
    }

    public static String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
