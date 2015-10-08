package com.carrotcreative.cream.strategies.generic;

import com.carrotcreative.cream.params.LoaderParams;
import com.carrotcreative.cream.loaders.single.SingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;

import java.io.Serializable;

public interface CacheStrategy<Identifier extends LoaderParams, Content extends Serializable> {

    void handleInitialLoad(final Identifier identifier, final boolean shouldCache, final CacheStrategyCallback callback);

    void handleCacheFailure(final Identifier identifier, final boolean hasExpirationRegard, Exception error, final CacheStrategyCallback callback);

    void handleSourceSuccess(final Identifier identifier, final Content object, SingleLoader<Identifier, Content> singleLoader, SingleLoaderCallback<Content> singleLoaderCallback);

    void handleSourceFailure(final Identifier identifier, Exception error, SingleLoader<Identifier, Content> singleLoader, SingleLoaderCallback<Content> singleLoaderCallback);

}