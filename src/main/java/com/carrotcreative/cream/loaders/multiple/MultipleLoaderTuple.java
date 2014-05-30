package com.carrotcreative.cream.loaders.multiple;

import java.io.Serializable;

public class MultipleLoaderTuple {

    public Serializable mContent;
    public boolean mFromCache;

    public MultipleLoaderTuple(Serializable content, boolean fromCache)
    {
        mContent = content;
        mFromCache = fromCache;
    }

}