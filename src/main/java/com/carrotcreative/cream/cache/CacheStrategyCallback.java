package com.carrotcreative.cream.cache;

public interface CacheStrategyCallback {

    public void handleFromCache();

    public void handleFromAPI();

    public void handleError(Exception error);

}