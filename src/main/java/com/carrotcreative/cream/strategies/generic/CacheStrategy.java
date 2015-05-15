package com.carrotcreative.cream.strategies.generic;

import com.carrotcreative.cream.params.LoaderParams;
import com.carrotcreative.cream.loaders.single.SingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;

import java.io.Serializable;

public interface CacheStrategy<T extends LoaderParams> {

    public void handleInitialLoad(final T identifier, final boolean shouldCache, final CacheStrategyCallback callback);

    public void handleCacheFailure(final T identifier, final boolean hasExpirationRegard, Exception error, final CacheStrategyCallback callback);

    public void handleSourceSuccess(final T identifier, final Serializable object, SingleLoader<T> singleLoader, SingleLoaderCallback singleLoaderCallback);

    public void handleSourceFailure(final T identifier, Exception error, SingleLoader<T> singleLoader, SingleLoaderCallback singleLoaderCallback);

}