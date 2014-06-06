package com.carrotcreative.cream_example.app.cache.loaders;

import android.content.Context;

import com.carrotcreative.cream.loaders.single.SingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;
import com.carrotcreative.cream.strategies.generic.CacheStrategy;
import com.carrotcreative.cream_example.app.net.GithubAPIBuilder;
import com.carrotcreative.cream_example.app.net.GithubUser;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * For simplicity, we're not using the DefaultLoader in this class.
 * A normal implementation would probably extend our DefaultLoader,
 * so we can avoid filling out all of the methods.
 */
public class GithubUserLoader extends SingleLoader<String> {

    public GithubUserLoader(Context context, CacheStrategy<String> cacheStrategy) {
        super(context, cacheStrategy);
    }

    /**
     * The directory this loader will cache to.
     * Relative to context.getCachedir().
     */
    @Override
    protected String getDirectory() {
        return "/users";
    }

    /**
     * File extension used to distinguish between different
     * loaders in the same directory.
     */
    @Override
    protected String getFileExtension() {
        return "user";
    }

    /**
     * The number of minutes from the time of cache
     * that we would ideally like to not use this by.
     *
     * The usage of this is more specifically defined
     * by our CacheStrategy
     */
    @Override
    protected long getExpirationMinutes() {
        return 10;
    }

    /**
     * The number of minutes after the user has
     * expired until we decide to delete it.
     */
    @Override
    protected long getTrashMinutes() {
        return 10;
    }

    /**
     * If we should cache or not.
     *
     * For example if we didn't want to cache users
     * whose usernames started with 'A', this would
     * be possible.
     */
    @Override
    public boolean shouldCache(String user) {
        return true;
    }

    /**
     * In this function we retrieve what we want to cache,
     * here I'm using retrofit -- but you can use whatever you want
     * as long as you can pack the result into a serializable object.
     */
    @Override
    protected void loadFromSource(final String user, final SingleLoaderCallback singleLoaderCallback) {

        final SingleLoader<String> thisLoader = this;

        GithubAPIBuilder.getAPI().getUser(user, new Callback<GithubUser>() {
            @Override
            public void success(GithubUser githubUser, Response response) {
                mCacheStrategy.handleSourceSuccess(user, githubUser, thisLoader, singleLoaderCallback);
            }

            @Override
            public void failure(RetrofitError error) {
                mCacheStrategy.handleSourceFailure(user, error, thisLoader, singleLoaderCallback);
            }
        });
    }

}