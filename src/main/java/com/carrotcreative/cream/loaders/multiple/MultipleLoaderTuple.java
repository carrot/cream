package com.carrotcreative.cream.loaders.multiple;

import java.io.Serializable;

public class MultipleLoaderTuple<Content extends Serializable> {

    private Content mContent;
    private boolean mFromCache;

    public MultipleLoaderTuple(Content content, boolean fromCache)
    {
        mContent = content;
        mFromCache = fromCache;
    }

    public Content getContent()
    {
        return mContent;
    }

    public boolean isFromCache()
    {
        return mFromCache;
    }

}