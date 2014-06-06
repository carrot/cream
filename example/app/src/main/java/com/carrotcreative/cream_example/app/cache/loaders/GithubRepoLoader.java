package com.carrotcreative.cream_example.app.cache.loaders;

import android.content.Context;

import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;
import com.carrotcreative.cream.strategies.generic.CacheStrategy;
import com.carrotcreative.cream_example.app.net.GithubAPIBuilder;
import com.carrotcreative.cream_example.app.net.GithubRepo;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * This is an example of both a class that uses a
 * custom loader (with defaults), and has an API call
 * with more than one parameter.
 *
 * -----  Default Loader -----
 * This loader implements our DefaultLoader, which has our default settings
 * built right into it, so all of our classes that implement it don't have to
 * override those methods.
 *
 * ----- Multiple params in API Call -----
 * As mentioned, this loader uses an API call with multiple parameters.
 * Here, we create our own custom object RepoDefinition, which contains all of
 * the needed paramaters for the API call.
 *
 * This is done this way to encapsulate the toString() functionality for all parameters.
 *
 * There may be parameters in your API call that are irrelevant to caching
 * (e.g. a session token) Don't include them in the toString().
 *
 * You'll need to implement the toString() method in a manner that uniquely identifies
 * the API call.
 */
public class GithubRepoLoader extends DefaultLoader<GithubRepoLoader.RepoDefinition>{

    public GithubRepoLoader(Context context, CacheStrategy cacheStrategy) {
        super(context, cacheStrategy);
    }

    @Override
    protected String getFileExtension() {
        return "repo";
    }

    @Override
    protected void loadFromSource(final RepoDefinition repo, final SingleLoaderCallback cb){
        final GithubRepoLoader thisLoader = this;

        GithubAPIBuilder.getAPI().getRepo(repo.owner, repo.name, new Callback<GithubRepo>() {
            @Override
            public void success(GithubRepo githubRepo, Response response) {
                mCacheStrategy.handleSourceSuccess(repo, githubRepo, thisLoader, cb);
            }

            @Override
            public void failure(RetrofitError error) {
                mCacheStrategy.handleSourceFailure(repo, error, thisLoader, cb);
            }
        });
    }

    /**
     * See "Multiple params in API Call"
     * section in GithubRepoLoader
     */
    public class RepoDefinition
    {
        public String owner;
        public String name;

        @Override
        public String toString()
        {
            return owner + "." + name;
        }
    }

}