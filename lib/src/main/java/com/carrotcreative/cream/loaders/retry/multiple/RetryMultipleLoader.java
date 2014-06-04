package com.carrotcreative.cream.loaders.retry.multiple;

import com.carrotcreative.cream.loaders.multiple.MultipleLoaderCallback;
import com.carrotcreative.cream.loaders.multiple.MultipleLoaderTuple;
import com.carrotcreative.cream.loaders.multiple.MultipleLoader;
import com.carrotcreative.cream.loaders.retry.RetryLoader;
import com.carrotcreative.cream.loaders.single.SingleLoader;

import java.util.ArrayList;

public class RetryMultipleLoader<Identifier> extends RetryLoader implements MultipleLoaderCallback {

    private RetryMultipleLoaderCallback mRetryMultipleLoaderCallback;
    private final MultipleLoader<Identifier> mMultiLoader;
    private final SingleLoader<Identifier> mSingleLoader;
    private ArrayList<Identifier> mIds;

    public RetryMultipleLoader(MultipleLoader<Identifier> multiLoader, SingleLoader<Identifier> singleLoader)
    {
        super();
        mMultiLoader = multiLoader;
        mSingleLoader = singleLoader;
    }

    public void loadSelf(final ArrayList<Identifier> ids, RetryMultipleLoaderCallback callback)
    {
        mIds = ids;
        mRetryMultipleLoaderCallback = callback;
        mMultiLoader.load(mIds, mSingleLoader, this);
    }

    @Override
    public void success(ArrayList<MultipleLoaderTuple> content) {
        mRetryMultipleLoaderCallback.success(content);
    }

    @Override
    public void failure(Exception error) {
        mAttemptNumber++;
        mRetryMultipleLoaderCallback.failedAttempt(mAttemptNumber);
    }

    @Override
    public void retryLoad() {
        mMultiLoader.load(mIds, mSingleLoader, this);
    }

}