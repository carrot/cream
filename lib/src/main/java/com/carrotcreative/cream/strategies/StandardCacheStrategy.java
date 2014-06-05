package com.carrotcreative.cream.strategies;

import android.content.Context;

import com.carrotcreative.cream.loaders.single.SingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;
import com.carrotcreative.cream.util.InternetStatus;

import java.io.Serializable;

/**
 * ===== Standard Caching Strategy =====
 *
 *  -> Cache files when they are loaded from the API
 *  -> Flag each type of file with a specific expiration, EXPIRATION_DAYS away
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
public class StandardCacheStrategy<T> implements CacheStrategy<T> {

    private Context mContext;

    public StandardCacheStrategy(Context context)
    {
        mContext = context;
    }

    @Override
    public void handleInitialLoad(T identifier, boolean shouldCache, CacheStrategyCallback callback)
    {
        if(shouldCache)
        {
            callback.handleFromCache();
        }
        else
        {
            callback.handleFromAPI();
        }
    }

    @Override
    public void handleCacheFailure(T identifier, boolean hasExpirationRegard, Exception error, CacheStrategyCallback callback)
    {
        if (hasExpirationRegard)
        {
            if (InternetStatus.isOnline(mContext))
                callback.handleFromAPI();
            else
                callback.handleFromCache();
        }
        else
        {
            callback.handleError(error);
        }
    }

    @Override
    public void handleSourceSuccess(T identifier, Serializable object, SingleLoader<T> singleLoader, SingleLoaderCallback singleLoaderCallback)
    {
        singleLoader.writeContent(identifier, object);
        singleLoaderCallback.success(object, false); // False, not from cache
        singleLoaderCallback.always();
    }

    @Override
    public void handleSourceFailure(T identifier, Exception error, SingleLoader<T> singleLoader, SingleLoaderCallback singleLoaderCallback)
    {
        if(singleLoader.shouldCache(identifier))
            singleLoader.loadFromCache(identifier, false, singleLoaderCallback);
        else
        {
            singleLoaderCallback.failure(error);
            singleLoaderCallback.always();
        }
    }

}