package com.carrotcreative.cream.loaders.retry.multiple;

import com.carrotcreative.cream.loaders.multiple.MultipleLoaderCallback;
import com.carrotcreative.cream.loaders.multiple.MultipleLoader;
import com.carrotcreative.cream.loaders.multiple.MultipleLoaderTuple;
import com.carrotcreative.cream.loaders.retry.RetryLoader;
import com.carrotcreative.cream.params.LoaderParams;
import com.carrotcreative.cream.loaders.single.SingleLoader;

import java.io.Serializable;
import java.util.ArrayList;

public class RetryMultipleLoader<Params extends LoaderParams, Content extends Serializable> extends RetryLoader implements MultipleLoaderCallback<Content> {

    private RetryMultipleLoaderCallback mRetryMultipleLoaderCallback;
    private final MultipleLoader<Params, Content> mMultiLoader;
    private final SingleLoader<Params, Content> mSingleLoader;
    private ArrayList<Params> mParamsList;

    public RetryMultipleLoader(MultipleLoader<Params, Content> multiLoader, SingleLoader<Params, Content> singleLoader)
    {
        super();
        mMultiLoader = multiLoader;
        mSingleLoader = singleLoader;
    }

    public void loadSelf(final ArrayList<Params> paramsList, RetryMultipleLoaderCallback callback)
    {
        mParamsList = paramsList;
        mRetryMultipleLoaderCallback = callback;
        mMultiLoader.load(mParamsList, mSingleLoader, this);
    }

    @Override
    public void success(ArrayList<MultipleLoaderTuple<Content>> content) {
        mRetryMultipleLoaderCallback.success(content);
    }

    @Override
    public void failure(Exception error) {
        mAttemptNumber++;
        mRetryMultipleLoaderCallback.failedAttempt(mAttemptNumber);
    }

    @Override
    public void always() {
        mRetryMultipleLoaderCallback.always();
    }

    @Override
    public void retryLoad() {
        mMultiLoader.load(mParamsList, mSingleLoader, this);
    }

}