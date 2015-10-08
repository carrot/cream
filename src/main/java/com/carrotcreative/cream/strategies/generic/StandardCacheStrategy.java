package com.carrotcreative.cream.strategies.generic;

import android.content.Context;

import com.carrotcreative.cream.params.LoaderParams;
import com.carrotcreative.cream.loaders.single.SingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;
import com.carrotcreative.cream.util.InternetStatus;

import java.io.Serializable;

public abstract class StandardCacheStrategy<Identifier extends LoaderParams, Content extends Serializable> implements CacheStrategy<Identifier, Content> {

    private Context mContext;

    public StandardCacheStrategy(Context context)
    {
        mContext = context;
    }

    @Override
    public void handleCacheFailure(Identifier identifier, boolean hasExpirationRegard, Exception error, CacheStrategyCallback callback)
    {
        if (hasExpirationRegard)
        {
            if (InternetStatus.isOnline(mContext))
                callback.handleFromAPI();
            else
                callback.handleFromCache();
        }
        else
        {
            callback.handleError(error);
        }
    }

    @Override
    public void handleSourceSuccess(Identifier identifier, Content object, SingleLoader<Identifier, Content> singleLoader, SingleLoaderCallback<Content> singleLoaderCallback)
    {
        singleLoader.writeContent(identifier, object);
        singleLoaderCallback.success(object, false); // False, not from cache
        singleLoaderCallback.always();
    }

    @Override
    public void handleSourceFailure(Identifier identifier, Exception error, SingleLoader<Identifier, Content> singleLoader, SingleLoaderCallback<Content> singleLoaderCallback)
    {
        if(singleLoader.shouldCache(identifier))
            singleLoader.loadFromCache(identifier, false, singleLoaderCallback);
        else
        {
            singleLoaderCallback.failure(error);
            singleLoaderCallback.always();
        }
    }

}