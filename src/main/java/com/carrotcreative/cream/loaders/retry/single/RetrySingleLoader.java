package com.carrotcreative.cream.loaders.retry.single;

import com.carrotcreative.cream.loaders.retry.RetryLoader;
import com.carrotcreative.cream.params.LoaderParams;
import com.carrotcreative.cream.loaders.single.SingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;

import java.io.Serializable;

public class RetrySingleLoader<Params extends LoaderParams> extends RetryLoader implements SingleLoaderCallback {

    private RetrySingleLoaderCallback mRetrySingleLoaderCallback;
    private final SingleLoader<Params> mLoader;
    private Params mParams;

    public RetrySingleLoader(SingleLoader<Params> loader)
    {
        super();
        mLoader = loader;
    }

    public void loadSelf(final Params params, RetrySingleLoaderCallback callback)
    {
        mParams = params;
        mRetrySingleLoaderCallback = callback;
        mLoader.loadSelf(mParams, this);
    }

    @Override
    public void retryLoad() {
        mLoader.loadSelf(mParams, this);
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

    @Override
    public void always() {
        mRetrySingleLoaderCallback.always();
    }

}