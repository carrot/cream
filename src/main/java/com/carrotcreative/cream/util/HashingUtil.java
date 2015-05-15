package com.carrotcreative.cream.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.support.annotation.NonNull;

public class HashingUtil
{
    private static final int CACHE_SIZE = 50;
    private static LruCache<String, String> mCache = new LruCache<>(CACHE_SIZE);

    private HashingUtil() {}

    /**
     *
     * @param secret The string that needs to be hashed.
     * @return The hashed value.
     */
    public static String genHash(@NonNull String secret)
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
            hashedString = new String(messageDigest.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            try
            {
                hashedString = Base64.encodeToString(secret.getBytes("UTF-8"), Base64.DEFAULT);
            }
            catch (UnsupportedEncodingException ee) {/* Do nothing */}

        }
        catch (UnsupportedEncodingException e) { /* Do nothing */}

        if(hashedString == null)
        {
            hashedString = secret.replaceAll("\\s+", "_");
        }
        return hashedString;

    }
}
