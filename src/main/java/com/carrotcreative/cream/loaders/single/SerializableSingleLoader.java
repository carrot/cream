package com.carrotcreative.cream.loaders.single;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.carrotcreative.cream.cache.CacheManager;
import com.carrotcreative.cream.tasks.ReadSerializableTask;
import com.carrotcreative.cream.tasks.WriteSerializableTask;

import java.io.Serializable;

/**
 * Caching Strategy
 *  -> Cache files when they are loaded from the API
 *  -> Flag each type of file with a specific expiration, getExpirationMinutes() away
 *  -> When the expiration date hits, we'll try to pull from the API again
 *  -> if we have network availability
 *          -> Hit the API
 *          -> If that somehow ended up failing
 *              -> Hit the cache with no regard to expiration
 *     else
 *          ->Hit the cache with no regard to expiration
 *
 *  -> Cache will be cleaned up after the file is TRASH_DAYS past expiration
 *     to prevent the cache from filling up too much.
 *
 *  -> Expired files will also be replaced in the event there is a successful
 *     API call to the same file.
 */
public abstract class SerializableSingleLoader<T> {

    protected Context mContext;

    public SerializableSingleLoader(Context context)
    {
        mContext = context;
    }

    public void loadSelf(final T identifier, final SingleCacheCallback callback){
        if(shouldCache(identifier))
        {
            loadFromCache(identifier, true, callback);
        }
        else
        {
            loadFromAPI(identifier, callback);
        }
    }

    //======== Abstract

    public abstract String getDirectory();

    public abstract String getFileExtension();

    public abstract long getExpirationMinutes();

    public abstract long getTrashMinutes();

    public abstract boolean shouldCache(T identifier);

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
                        //Try again if we had regard for cache before
                        if (hasExpirationRegard) {
                            if (thisLoader.isOnline())
                                loadFromAPI(identifier, singleCacheCallback);
                            else
                                loadFromCache(identifier, false, singleCacheCallback);
                        } else {
                            singleCacheCallback.failure(error);
                        }
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

    //======== Helper

    /**
     * Returns true if we have internet connectivity, false otherwise
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}