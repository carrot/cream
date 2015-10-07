package com.carrotcreative.cream.strategies.generic;

import com.carrotcreative.cream.params.LoaderParams;
import com.carrotcreative.cream.loaders.single.SingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;

import java.io.Serializable;

public interface CacheStrategy<T extends LoaderParams, C extends Serializable> {

    void handleInitialLoad(final T identifier, final boolean shouldCache, final CacheStrategyCallback callback);

    void handleCacheFailure(final T identifier, final boolean hasExpirationRegard, Exception error, final CacheStrategyCallback callback);

    void handleSourceSuccess(final T identifier, final C object, SingleLoader<T, C> singleLoader, SingleLoaderCallback<C> singleLoaderCallback);

    void handleSourceFailure(final T identifier, Exception error, SingleLoader<T, C> singleLoader, SingleLoaderCallback<C> singleLoaderCallback);

}