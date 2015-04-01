package com.carrotcreative.cream.loaders.multiple;

import java.io.Serializable;

public class MultipleLoaderTuple {

    private final Serializable mContent;
    private final boolean mFromCache;

    public MultipleLoaderTuple(Serializable content, boolean fromCache)
    {
        mContent = content;
        mFromCache = fromCache;
    }
}