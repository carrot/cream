package com.carrotcreative.cream.loaders.multiple;

import java.io.Serializable;

public class MultipleLoaderTuple<Content extends Serializable> {

    public Content mContent;
    public boolean mFromCache;

    public MultipleLoaderTuple(Content content, boolean fromCache)
    {
        mContent = content;
        mFromCache = fromCache;
    }

}