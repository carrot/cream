package com.carrotcreative.cream.loaders.single;

import android.content.Context;

import com.carrotcreative.cream.cache.CacheManager;
import com.carrotcreative.cream.strategies.CacheStrategy;
import com.carrotcreative.cream.strategies.CacheStrategyCallback;
import com.carrotcreative.cream.tasks.ReadSerializableTask;
import com.carrotcreative.cream.tasks.WriteSerializableTask;

import java.io.Serializable;

public abstract class SerializableSingleLoader<T> {

    protected Context mContext;
    protected CacheStrategy<T> mCacheStrategy;

    public SerializableSingleLoader(Context context, CacheStrategy<T> cacheStrategy)
    {
        mContext = context;
        mCacheStrategy = cacheStrategy;
    }

    public void loadSelf(final T identifier, final SingleCacheCallback callback){
        handleInitialLoad(identifier, callback);
    }

    protected void handleInitialLoad(final T identifier, final SingleCacheCallback callback)
    {
        mCacheStrategy.handleInitialLoad(identifier, shouldCache(identifier), new CacheStrategyCallback() {
            @Override
            public void handleFromCache() {
                loadFromCache(identifier, true, callback);
            }

            @Override
            public void handleFromAPI() {
                loadFromAPI(identifier, callback);
            }

            @Override
            public void handleError(Exception error) {
                callback.failure(error);
            }
        });
    }

    protected void handleCacheFailure(final T identifier, final boolean hasExpirationRegard, final SingleCacheCallback singleCacheCallback, Exception error)
    {
        mCacheStrategy.handleCacheFailure(identifier, hasExpirationRegard, error, new CacheStrategyCallback() {
            @Override
            public void handleFromCache() {
                loadFromCache(identifier, false, singleCacheCallback);
            }

            @Override
            public void handleFromAPI() {
                loadFromAPI(identifier, singleCacheCallback);
            }

            @Override
            public void handleError(Exception error) {
                singleCacheCallback.failure(error);
            }
        });
    }

    //======== Abstract

    protected abstract String getDirectory();

    protected abstract String getFileExtension();

    protected abstract long getExpirationMinutes();

    protected abstract long getTrashMinutes();

    protected abstract boolean shouldCache(T identifier);

    protected abstract void loadFromAPI(T identifier, SingleCacheCallback cb);

    protected abstract String getPrefix(T identifier);

    //======= Read

    protected void loadFromCache(final T identifier, final boolean hasExpirationRegard, final SingleCacheCallback singleCacheCallback)
    {
        final String prefix = getPrefix(identifier);
        final SerializableSingleLoader thisLoader = this;

        CacheManager.getInstance(mContext).readSerializable(getDirectory(), getFileExtension(), prefix, hasExpirationRegard,
                new ReadSerializableTask.ReadSerializableCallback() {
                    @Override
                    public void success(Serializable object) {
                        singleCacheCallback.success(object, true);
                    }

                    @Override
                    public void failure(Exception error) {
                        handleCacheFailure(identifier, hasExpirationRegard, singleCacheCallback, error);
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
    protected void writeContent(T identifier, Serializable content)
    {
        writeContentRecursive(mWriteAttempts, identifier, content);
    }

    private void writeContentRecursive(final int attemptsRemaining, final T identifier, final Serializable content) {
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
                }
            );
        }
    }

    //======= Cleanup

    /**
     * //TODO run this somewhere
     */
    public void runCleanup()
    {
        CacheManager.getInstance(mContext).runTrashCleanup(getDirectory(), getFileExtension(), getTrashMinutes());
    }

}