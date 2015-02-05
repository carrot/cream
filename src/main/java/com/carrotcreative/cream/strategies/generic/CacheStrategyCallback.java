package com.carrotcreative.cream.strategies.generic;

public interface CacheStrategyCallback {

    public void handleFromCache();

    public void handleFromAPI();

    public void handleError(Exception error);

}