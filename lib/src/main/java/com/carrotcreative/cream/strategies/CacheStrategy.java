package com.carrotcreative.cream.strategies;

public interface CacheStrategy<T> {

    public void handleInitialLoad(final T identifier, final boolean shouldCache, final CacheStrategyCallback callback);

    public void handleCacheFailure(final T identifier, final boolean hasExpirationRegard, Exception error, final CacheStrategyCallback callback);

}