package com.carrotcreative.cream.loaders.retry.single;

import com.carrotcreative.cream.loaders.retry.RetryLoader;
import com.carrotcreative.cream.loaders.single.SingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;

import java.io.Serializable;

public class RetrySingleLoader<Identifier> extends RetryLoader implements SingleLoaderCallback {

    private RetrySingleLoaderCallback mRetrySingleLoaderCallback;
    private final SingleLoader<Identifier> mLoader;
    private final Identifier mIdentifier;

    public RetrySingleLoader(SingleLoader<Identifier> loader, final Identifier identifier, RetrySingleLoaderCallback callback)
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