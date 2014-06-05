CREAM
-----

> **Note:** This project is in early development, and versioning is a little different. [Read this](http://markup.im/#q4_cRZ1Q) for more details.

Cream is a caching library for Android.

CREAM is not a "plug into your HTTP-Client and forget about it" type library.  CREAM is focused on flexibility above all else.  That being said, CREAM is a little more verbose than other alternatives (although it's still not bad at all).

For each of your API calls (or whatever that's pulled externally that needs to be cached) you'll need to make a Loader.

###Cache Strategies

Before we get into talking about loaders, let's take a quick look at Cache Strategies.

###SingleLoader - Setup

Single loaders are the bread and butter of CREAM.  They're used directly to make a single cached external call, and are very simply passed into RetryLoaders and MultipleLoaders to get them up and running really quickly.

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
                // Don't forget to cache the content, or else this library is useless!
                writeContent(user, githubUser);
                
                //This is really important that you call these -- let the callback know
                singleLoaderCallback.success(githubUser, false); //False -- Not from Cache
                singleLoaderCallback.always();
            }

            @Override
            public void failure(RetrofitError error) {
                // You might have to handle other things here, 
                // but the structure of your API falures should always
                // look something like this.
                if(shouldCache(user))
                    loadFromCache(user, false, singleLoaderCallback);
                else
                {
                    singleLoaderCallback.failure(error);
                    singleLoaderCallback.always();
                }
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

###SingleLoader - Usage

```java
// Creating a StandardCacheStrategy object to plug into the Loader
CacheStrategy<String> cacheStrategy = new StandardCacheStrategy<String>(getContext());

GithubUserLoader loader = new GithubUserLoader(getContext(), cacheStrategy);
loader.loadSelf("BrandonRomano", new SingleLoaderCallback() {
    @Override
    public void success(Serializable serializable, boolean fromCache) {
        // Success!  We have the user here, do whatever you please to them.
        GithubUser user = (GithubUser) serializable;
    }

    @Override
    public void failure(Exception e) {
        //Failure, handle this however you would like.
    }
});
```

### Multiple Loaders

Multiple loaders are really useful when an API you're using lacks the functionality to make a call for multiple data points at once.  This functionality in CREAM makes it feel like the API functionality actually exists.

After you've got your single loader set up, multiple loaders are really simple to get up and running.

```java
// Creating an ArrayList of all of the users we will download
ArrayList<String> githubUserNames = new ArrayList<String>();
githubUserNames.add("BrandonRomano");
githubUserNames.add("pruett");

// Creating a StandardCacheStrategy object to plug into the Loader
CacheStrategy<String> cacheStrategy = new StandardCacheStrategy<String>(getContext());

// Creating the single loader
final GithubUserLoader singleLoader = new GithubUserLoader(getContext(), cacheStrategy);

// Creating a multiple loader with a STRICT_POLICY (all successful or loader will fail)
final MultipleLoader<String> multipleLoader = new MultipleLoader<String>(MultipleLoader.STRICT_POLICY);

// Load!
multipleLoader.load(githubUserNames, singleLoader, new MultipleLoaderCallback() {
    @Override
    public void success(ArrayList<MultipleLoaderTuple> loaderTuples) {
        //TODO handle success, serializable objects are packed into the tuples
    }

    @Override
    public void failure(Exception e) {
        //TODO handle failure
    }

    @Override
    public void always() {
        //TODO handle always
    }
});

```

###Retry Loaders

Retry Loaders are useful as mobile internet is notorious for being unstable.  Retry loaders make it easy to retry a call in the event of a failure.

```java
// Creating a StandardCacheStrategy object to plug into the Loader
CacheStrategy<String> cacheStrategy = new StandardCacheStrategy<String>(getContext());

// Creating the loader
final GithubUserLoader singleLoader = new GithubUserLoader(getContext(), cacheStrategy);

// Creating a retry loader
final RetrySingleLoader<String> retrySingleLoader = new RetrySingleLoader<String>(singleLoader);

// Load!
retrySingleLoader.loadSelf("BrandonRomano", new RetrySingleLoaderCallback() {
    @Override
    public void success(Serializable serializable, boolean b) {
        // Success!  We have the user here, do whatever you please to them.
        GithubUser user = (GithubUser) serializable;
    }

    @Override
    public void failedAttempt(int attemptNumber) {
        // Add your custom logic to calling retry here, but here's an example
        if(attemptNumber < MAX_RETRY_ATTEMPTS)
        {
            retrySingleLoader.retry();
        }
        else
        {
            //TODO handle 
        }
    }

    @Override
    public void always() {
        //TODO handle if you need this
    }
}
```

###Multiple Retry Loaders

You might run into a situation in which you need a multiple loader that is also a retry loader.  Don't worry, CREAM's has got you covered.

```java
// Creating a StandardCacheStrategy object to plug into the Loader
CacheStrategy<String> cacheStrategy = new StandardCacheStrategy<String>(getContext());

// Creating the loader
final GithubUserLoader singleLoader = new GithubUserLoader(getContext(), cacheStrategy);

// Creating a multiple loader with a STRICT_POLICY
final MultipleLoader<String> multipleLoader = new MultipleLoader<String>(MultipleLoader.STRICT_POLICY);

// Creating a retryMultipleLoader
final RetryMultipleLoader<String> retryMultipleLoader = new RetryMultipleLoader<String>(multipleLoader, singleLoader);

// Load!
retryMultipleLoader.loadSelf(mGithubUserNames, new RetryMultipleLoaderCallback() {
    @Override
    public void success(ArrayList<MultipleLoaderTuple> loaderTuples) {
        // Serializable objects packed into the loaderTuples
    }

    @Override
    public void failedAttempt(int attemptNumber) {
        if(attemptNumber < MAX_RETRY_ATTEMPTS)
        {
            retryMultipleLoader.retry();
        }
        else
        {
            //TODO handle error
        }
    }

    @Override
    public void always() {
        signal.countDown();
    }

});

```

###Too verbose?

You'll most likely find that your application has some type of caching default, so feel free to extend the SingleLoader class to implement some of the methods as defaults and override them as needed.

###Example

The example goes over most of the features, and would be the best way to get started.  It's included in the project.