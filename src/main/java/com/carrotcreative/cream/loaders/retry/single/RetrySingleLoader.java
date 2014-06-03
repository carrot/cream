package com.carrotcreative.cream.loaders.retry.single;

import com.carrotcreative.cream.loaders.retry.RetryLoader;
import com.carrotcreative.cream.loaders.single.SingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;

import java.io.Serializable;

public class RetrySingleLoader<Identifier> extends RetryLoader implements SingleLoaderCallback {

    private RetrySingleLoaderCallback mRetrySingleLoaderCallback;
    private final SingleLoader<Identifier> mLoader;
    private Identifier mIdentifier;

    public RetrySingleLoader(SingleLoader<Identifier> loader)
    {
        super();
        mLoader = loader;
    }

    public void loadSelf(final Identifier identifier, RetrySingleLoaderCallback callback)
    {
        mIdentifier = identifier;
        mRetrySingleLoaderCallback = callback;
        mLoader.loadSelf(mIdentifier, this);
    }

    @Override
    public void retryLoad() {
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