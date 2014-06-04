package com.carrotcreative.cream.strategies;

public interface CacheStrategyCallback {

    public void handleFromCache();

    public void handleFromAPI();

    public void handleError(Exception error);

}