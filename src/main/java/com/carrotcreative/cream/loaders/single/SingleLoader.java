package com.carrotcreative.cream.loaders.single;

import android.content.Context;

import com.carrotcreative.cream.cache.CacheManager;
import com.carrotcreative.cream.params.LoaderParams;
import com.carrotcreative.cream.strategies.generic.CacheStrategy;
import com.carrotcreative.cream.strategies.generic.CacheStrategyCallback;
import com.carrotcreative.cream.tasks.ReadSerializableTask;
import com.carrotcreative.cream.tasks.WriteSerializableTask;
import com.carrotcreative.cream.util.HashingUtil;

import java.io.Serializable;

public abstract class SingleLoader<Params extends LoaderParams, Content extends Serializable> {

    protected Context mContext;
    protected CacheStrategy<Params, Content> mCacheStrategy;

    public SingleLoader(Context context, CacheStrategy<Params, Content> cacheStrategy)
    {
        mContext = context;
        mCacheStrategy = cacheStrategy;
    }

    public void loadSelf(final Params params, final SingleLoaderCallback callback){
        handleInitialLoad(params, callback);
    }

    protected void handleInitialLoad(final Params params, final SingleLoaderCallback callback)
    {
        mCacheStrategy.handleInitialLoad(params, shouldCache(params), new CacheStrategyCallback() {
            @Override
            public void handleFromCache() {
                loadFromCache(params, true, callback);
            }

            @Override
            public void handleFromAPI() {
                loadFromSource(params, callback);
            }

            @Override
            public void handleError(Exception error) {
                callback.failure(error);
                callback.always();
            }
        });
    }

    protected void handleCacheFailure(final Params identifier, final boolean hasExpirationRegard, final SingleLoaderCallback<Content> singleLoaderCallback, Exception error)
    {
        mCacheStrategy.handleCacheFailure(identifier, hasExpirationRegard, error, new CacheStrategyCallback() {
            @Override
            public void handleFromCache() {
                loadFromCache(identifier, false, singleLoaderCallback);
            }

            @Override
            public void handleFromAPI() {
                loadFromSource(identifier, singleLoaderCallback);
            }

            @Override
            public void handleError(Exception error) {
                singleLoaderCallback.failure(error);
                singleLoaderCallback.always();
            }
        });
    }

    private String getPrefix(Params identifier)
    {
        return HashingUtil.getHash(identifier.getIdentifier());
    }

    //======== Abstract

    protected abstract String getDirectory();

    protected abstract String getFileExtension();

    protected abstract long getExpirationMinutes();

    protected abstract long getTrashMinutes();

    public abstract boolean shouldCache(Params identifier);

    protected abstract void loadFromSource(Params identifier, SingleLoaderCallback cb);

    //======= Read

    public void loadFromCache(final Params identifier, final boolean hasExpirationRegard, final SingleLoaderCallback singleLoaderCallback)
    {
        final String prefix = getPrefix(identifier);

        CacheManager.getInstance(mContext).readSerializable(getDirectory(), getFileExtension(), prefix, hasExpirationRegard,
                new ReadSerializableTask.ReadSerializableCallback() {
                    @Override
                    public void success(Serializable object) {
                        singleLoaderCallback.success(object, true);
                    }

                    @Override
                    public void failure(Exception error) {
                        handleCacheFailure(identifier, hasExpirationRegard, singleLoaderCallback, error);
                    }

                    @Override
                    public void always() {
                        singleLoaderCallback.always();
                    }
                }
        );
    }

    //======= Write

    /**
     * This can be overwritten in a subclass if you feel
     * 3 isn't the right number
     */
    protected int mWriteAttempts = 3;

    /**
     * Just writing content to cache, it shouldn't really ever fail
     * but we're giving it mWriteAttempts attempts at it.
     */
    public void writeContent(Params identifier, Serializable content)
    {
        writeContentRecursive(mWriteAttempts, identifier, content);
    }

    private void writeContentRecursive(final int attemptsRemaining, final Params identifier, final Serializable content) {
        final String prefix = getPrefix(identifier);
        if (attemptsRemaining > 0) {

            CacheManager.getInstance(mContext).writeSerializable(getDirectory(), getExpirationMinutes(),
                getFileExtension(), prefix, content, new WriteSerializableTask.WriteSerializableCallback(){
                    @Override
                    public void success() { /* Do nothing */ }

                    @Override
                    public void failure(Exception error) {
                        writeContentRecursive(attemptsRemaining - 1, identifier, content);
                    }

                    @Override
                    public void always() { /* Do nothing */}
                }
            );
        }
    }

    //======= Cleanup

    public void runCleanup()
    {
        CacheManager.getInstance(mContext).runTrashCleanup(getDirectory(), getFileExtension(), getTrashMinutes());
    }

}