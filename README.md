CREAM
-----

> **Note:** This project is in early development, and versioning is a little different. [Read this](http://markup.im/#q4_cRZ1Q) for more details.

> An example project can be found [here](https://github.com/carrot/CREAM-example).

Cream is a caching library for Android.

CREAM is not a "plug into your HTTP-Client and forget about it" type library.  CREAM is focused on flexibility above all else.  That being said, CREAM is a little more verbose than other alternatives (although it's still not bad at all).

For each of your API calls (or whatever that's pulled externally that needs to be cached) you'll need to make a Loader.

###SingleLoader

```java
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
    protected boolean shouldCache(String user) {
        return true;
    }

    /**
     * In this function we retrieve what we want to cache,
     * here I'm using retrofit -- but you can use whatever you want
     * as long as you can pack the result into a serializable object.
     */
    @Override
    protected void loadFromSource(final String user, final SingleLoaderCallback singleLoaderCallback) {

        GithubAPIBuilder.getAPI().getUser(user, new Callback<GithubUser>() {
            @Override
            public void success(GithubUser githubUser, Response response) {
                writeContent(user, githubUser);
                singleLoaderCallback.success(githubUser, false); //False -- Not from Cache
            }

            @Override
            public void failure(RetrofitError error) {
                if(shouldCache(user))
                    loadFromCache(user, false, singleLoaderCallback);
                else
                    singleLoaderCallback.failure(error);
            }
        });
    }

    /**
     * This is the unique identifier for the user
     * to identify it in cache.
     */
    @Override
    protected String getPrefix(String user) {
        return user;
    }

}
```

To use the SingleLoader:

```java
GithubUserLoader loader = new GithubUserLoader(this, cacheStrategy);
loader.loadSelf(userName, new SingleLoaderCallback() {
    @Override
    public void success(Serializable serializable, boolean b) {
        // Success!  We have the user here, do whatever you please to them.
        GithubUser user = (GithubUser) serializable;
    }

    @Override
    public void failure(Exception e) {
        //Failure, handle this however you would like.
    }
});
```

### Other Features

- Multiple Loaders 
  * Allows you to spawn multiple loaders to hit an API more than once
- Repeat Loaders
  * Allows you to keep trying the API until you decide to stop. 

The example goes over most of the features, and would be the best way to get started, so check it out [here](https://github.com/carrot/CREAM-example).
