package com.carrotcreative.cream.strategies;

import com.carrotcreative.cream.loaders.single.SingleCacheCallback;

public interface CacheStrategy<T> {

    public void handleInitialLoad(final T identifier, final boolean shouldCache, final CacheStrategyCallback callback);

    public void handleCacheFailure(final T identifier, final boolean hasExpirationRegard, Exception error, final CacheStrategyCallback callback);

}