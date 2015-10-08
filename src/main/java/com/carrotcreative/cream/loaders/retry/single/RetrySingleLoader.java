package com.carrotcreative.cream.loaders.retry.single;

import com.carrotcreative.cream.loaders.retry.RetryLoader;
import com.carrotcreative.cream.params.LoaderParams;
import com.carrotcreative.cream.loaders.single.SingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;

import java.io.Serializable;

public class RetrySingleLoader<Params extends LoaderParams, Content extends Serializable> extends RetryLoader implements SingleLoaderCallback<Content> {

    private RetrySingleLoaderCallback mRetrySingleLoaderCallback;
    private final SingleLoader<Params, Content> mLoader;
    private Params mParams;

    public RetrySingleLoader(SingleLoader<Params, Content> loader)
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
    public void success(Content content, boolean fromCache) {
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