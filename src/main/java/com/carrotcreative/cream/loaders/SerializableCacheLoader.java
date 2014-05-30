package com.carrotcreative.cream.loaders;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.carrotcreative.cream.cache.CacheManager;
import com.carrotcreative.cream.tasks.ReadSerializableTask;
import com.carrotcreative.cream.tasks.WriteSerializableTask;

import java.io.Serializable;

public abstract class SerializableCacheLoader<T> {

    protected Context mContext;

    public SerializableCacheLoader(Context context)
    {
        mContext = context;
    }

    public void loadSelf(final T identifier, final StandardIDCallback callback){
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

    protected abstract void loadFromAPI(T identifier, StandardIDCallback cb);

    protected abstract String getPrefix(T identifier);

    //======= Read

    protected void loadFromCache(final T identifier, final boolean hasExpirationRegard, final StandardIDCallback standardIDCallback)
    {
        final String prefix = getPrefix(identifier);
        final SerializableCacheLoader thisLoader = this;

        CacheManager.getInstance(mContext).readSerializable(getDirectory(), getFileExtension(), prefix, hasExpirationRegard,
                new ReadSerializableTask.ReadSerializableCallback() {
                    @Override
                    public void success(Serializable object) {
                        standardIDCallback.success(object, true);
                    }

                    @Override
                    public void failure(Exception error) {
                        //Try again if we had regard for cache before
                        if (hasExpirationRegard) {
                            if (thisLoader.isOnline())
                                loadFromAPI(identifier, standardIDCallback);
                            else
                                loadFromCache(identifier, false, standardIDCallback);
                        } else {
                            standardIDCallback.failure(error);
                        }
                    }
                }
        );
    }

    //======= Write

    /**
     * This can be overwritten in a subclass if you feel
     * 3 isn't the right number for you
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

    //======== Callback

    public interface StandardIDCallback {
        void success(Serializable content, boolean fromCache);
        void failure(Exception error);
    }

}