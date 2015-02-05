package com.carrotcreative.cream.loaders.single;

import android.content.Context;

import com.carrotcreative.cream.cache.CacheManager;
import com.carrotcreative.cream.strategies.generic.CacheStrategy;
import com.carrotcreative.cream.strategies.generic.CacheStrategyCallback;
import com.carrotcreative.cream.tasks.ReadSerializableTask;
import com.carrotcreative.cream.tasks.WriteSerializableTask;

import java.io.Serializable;

public abstract class SingleLoader<T> {

    protected Context mContext;
    protected CacheStrategy<T> mCacheStrategy;

    public SingleLoader(Context context, CacheStrategy<T> cacheStrategy)
    {
        mContext = context;
        mCacheStrategy = cacheStrategy;
    }

    public void loadSelf(final T identifier, final SingleLoaderCallback callback){
        handleInitialLoad(identifier, callback);
    }

    protected void handleInitialLoad(final T identifier, final SingleLoaderCallback callback)
    {
        mCacheStrategy.handleInitialLoad(identifier, shouldCache(identifier), new CacheStrategyCallback() {
            @Override
            public void handleFromCache() {
                loadFromCache(identifier, true, callback);
            }

            @Override
            public void handleFromAPI() {
                loadFromSource(identifier, callback);
            }

            @Override
            public void handleError(Exception error) {
                callback.failure(error);
                callback.always();
            }
        });
    }

    protected void handleCacheFailure(final T identifier, final boolean hasExpirationRegard, final SingleLoaderCallback singleLoaderCallback, Exception error)
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

    private String getPrefix(T identifier)
    {
        return identifier.toString();
    }

    //======== Abstract

    protected abstract String getDirectory();

    protected abstract String getFileExtension();

    protected abstract long getExpirationMinutes();

    protected abstract long getTrashMinutes();

    public abstract boolean shouldCache(T identifier);

    protected abstract void loadFromSource(T identifier, SingleLoaderCallback cb);

    //======= Read

    public void loadFromCache(final T identifier, final boolean hasExpirationRegard, final SingleLoaderCallback singleLoaderCallback)
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
    public void writeContent(T identifier, Serializable content)
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