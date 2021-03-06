package com.carrotcreative.cream.strategies;

import android.content.Context;

import com.carrotcreative.cream.params.LoaderParams;
import com.carrotcreative.cream.strategies.generic.CacheStrategyCallback;
import com.carrotcreative.cream.strategies.generic.StandardCacheStrategy;

import java.io.Serializable;

/**
 * ===== Source Preferred Strategy =====
 *
 *  - Cache files when they are loaded from the API
 *  - Expiration doesn't matter with this strategy, you should probably set it to 0
 *    so your Trash date is relative to the time of cache
 *  - Older files will also be replaced in the event there is a successful
 *    API call to the same file.
 *
 *  -> if we have network availability
 *          -> Hit the API
 *          -> If that somehow ended up failing
 *              -> Hit the cache with no regard to expiration
 *     else
 *          ->Hit the cache with no regard to expiration
 */
public class SourcePreferred<Identifier extends LoaderParams, Content extends Serializable> extends StandardCacheStrategy<Identifier, Content> {

    public SourcePreferred(Context context) {
        super(context);
    }

    @Override
    public void handleInitialLoad(Identifier identifier, boolean shouldCache, CacheStrategyCallback callback)
    {
        callback.handleFromAPI();
    }

}