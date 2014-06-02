package com.carrotcreative.cream.loaders.retry;

import com.carrotcreative.cream.loaders.multiple.MultipleCacheCallback;
import com.carrotcreative.cream.loaders.multiple.MultipleLoaderTuple;
import com.carrotcreative.cream.loaders.multiple.SerializableMultipleLoader;
import com.carrotcreative.cream.loaders.single.SerializableSingleLoader;

import java.util.ArrayList;

public class RetryMultipleLoader<Identifier> extends RetryLoader implements MultipleCacheCallback{

    private RetryMultipleLoaderCallback mRetryMultipleLoaderCallback;
    private final SerializableMultipleLoader<Identifier> mMultiLoader;
    private final SerializableSingleLoader<Identifier> mSingleLoader;
    private final ArrayList<Identifier> mIds;

    public RetryMultipleLoader(SerializableMultipleLoader<Identifier> multiLoader, SerializableSingleLoader<Identifier> singleLoader, final ArrayList<Identifier> ids, RetryMultipleLoaderCallback callback)
    {
        super();
        mMultiLoader = multiLoader;
        mSingleLoader = singleLoader;
        mIds = ids;
        mRetryMultipleLoaderCallback = callback;
    }

    public void loadSelf()
    {
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

}