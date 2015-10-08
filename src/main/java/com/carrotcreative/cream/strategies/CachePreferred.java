package com.carrotcreative.cream.strategies;

import android.content.Context;

import com.carrotcreative.cream.params.LoaderParams;
import com.carrotcreative.cream.strategies.generic.CacheStrategyCallback;
import com.carrotcreative.cream.strategies.generic.StandardCacheStrategy;

import java.io.Serializable;

/**
 * ===== Cache Preferred Strategy =====
 *
 *  - Cache files when they are loaded from the API
 *  - Flag each type of file with a specific expiration, EXPIRATION_DAYS away
 *  - When the expiration date hits, we'll try to pull from the API again
 *  - Older files will also be replaced in the event there is a successful
 *    API call to the same file.
 *
 *  -> Try to hit cache with regard to expiration
 *      -> If we find something we're done, otherwise continue
 *
 *  -> if we have network availability
 *          -> Hit the API
 *          -> If that somehow ended up failing
 *              -> Hit the cache with no regard to expiration
 *     else
 *          ->Hit the cache with no regard to expiration
 */
public class CachePreferred<Identifier extends LoaderParams, Content extends Serializable> extends StandardCacheStrategy<Identifier, Content> {

    public CachePreferred(Context context) {
        super(context);
    }

    @Override
    public void handleInitialLoad(Identifier identifier, boolean shouldCache, CacheStrategyCallback callback)
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

}