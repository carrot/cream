package com.carrotcreative.cream.loaders.retry;

import com.carrotcreative.cream.loaders.single.SerializableSingleLoader;
import com.carrotcreative.cream.loaders.single.SingleCacheCallback;

import java.io.Serializable;

public class RetrySingleLoader<Identifier> extends RetryLoader implements SingleCacheCallback{

    private RetrySingleLoaderCallback mRetrySingleLoaderCallback;
    private final SerializableSingleLoader<Identifier> mLoader;
    private final Identifier mIdentifier;

    public RetrySingleLoader(SerializableSingleLoader<Identifier> loader, final Identifier identifier, RetrySingleLoaderCallback callback)
    {
        super();
        mLoader = loader;
        mIdentifier = identifier;
        mRetrySingleLoaderCallback = callback;
    }

    @Override
    public void loadSelf()
    {
        mLoader.loadSelf(mIdentifier, this);
    }

    @Override
    public void success(Serializable content, boolean fromCache) {
        mRetrySingleLoaderCallback.success(content, fromCache);
    }

    @Override
    public void failure(Exception error) {
        mAttemptNumber++;
        mRetrySingleLoaderCallback.failedAttempt(mAttemptNumber);
    }

}