package com.carrotcreative.cream.loaders.multiple;

import com.carrotcreative.cream.params.LoaderParams;
import com.carrotcreative.cream.loaders.single.SingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MultipleLoader<Params extends LoaderParams> {

    /** This policy requires everything to be downloaded, or else it fails */
    public static final int STRICT_POLICY = 1;

    /** This policy tries to download everything,
        but is still successful even if only one element is downloaded */
    public static final int RELAXED_POLICY = 2;

    //===================================

    private ArrayList<MultipleLoaderTuple> mLoaderTuples;
    private AtomicInteger mFinishedCounter;
    private final int mDownloadPolicy;
    private int mTotalToLoad;


    public MultipleLoader(int downloadPolicy) {
        mDownloadPolicy = downloadPolicy;
    }

    public void load(final ArrayList<Params> paramsList, SingleLoader<Params> loader, final MultipleLoaderCallback multipleCallback) {
        mLoaderTuples = new ArrayList<MultipleLoaderTuple>();
        mFinishedCounter = new AtomicInteger(0);
        mTotalToLoad = paramsList.size();

        for (final Params param : paramsList) {
            loader.loadSelf(param, new SingleLoaderCallback() {

                @Override
                public void success(Serializable content, boolean fromCache) {
                    //Adding to our loaderTuples
                    MultipleLoaderTuple tuple = new MultipleLoaderTuple(content, fromCache);
                    mLoaderTuples.add(tuple);
                    checkFinished(multipleCallback);
                }

                @Override
                public void failure(Exception error) {

                    checkFinished(multipleCallback);
                }

                @Override
                public void always() { /* Do nothing */ }

            });
        }
    }

    private void checkFinished(MultipleLoaderCallback callback)
    {
        //Increment the counter, something just finished
        mFinishedCounter.incrementAndGet();

        switch(mDownloadPolicy)
        {
            case STRICT_POLICY:
                checkStrict(callback);
                break;

            case RELAXED_POLICY:
                checkRelaxed(callback);
                break;
        }
    }

    private void checkRelaxed(MultipleLoaderCallback callback)
    {
        //If we're finished
        if(mFinishedCounter.get() >= mTotalToLoad)
        {
            if(mLoaderTuples.size() != 0) //We have some: success
            {
                callback.success(mLoaderTuples);
            }
            else
            {
                callback.failure(new Exception("Failed to download using RELAXED_POLICY"));
            }

            callback.always();
        }
    }

    private void checkStrict(MultipleLoaderCallback callback)
    {
        if(mFinishedCounter.get() >= mTotalToLoad)
        {
            if(mLoaderTuples.size() == mTotalToLoad) //We download them all: success
            {
                callback.success(mLoaderTuples);
            }
            else
            {
                callback.failure(new Exception("Failed to download using STRICT_POLICY"));
            }

            callback.always();
        }
    }

}