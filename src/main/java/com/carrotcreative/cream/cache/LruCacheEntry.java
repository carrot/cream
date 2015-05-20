package com.carrotcreative.cream.cache;

import com.carrotcreative.cream.util.ObjectSizeUtil;

import java.io.Serializable;

public class LruCacheEntry
{
    private Serializable mObject;
    private int mSize;

    public LruCacheEntry(Serializable object)
    {
        mObject = object;
        mSize = ObjectSizeUtil.getObjectSize(object);
    }

    public Serializable getValue()
    {
        return mObject;
    }

    public int getSize()
    {
        return mSize;
    }
}
