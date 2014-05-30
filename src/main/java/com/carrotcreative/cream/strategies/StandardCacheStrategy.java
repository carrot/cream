package com.carrotcreative.cream.strategies;

import android.content.Context;

import com.carrotcreative.cream.util.InternetStatus;

/* TODO add description */
public class StandardCacheStrategy<T> implements CacheStrategy<T>{

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
    public void handleCacheFailure(T identifier, boolean hasExpirationRegard, Exception error, CacheStrategyCallback callback) {
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

}