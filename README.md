CREAM
-----

Cream is a caching library for Android.

CREAM is not a "plug into your HTTP-Client and forget about it" type library.  CREAM is focused on flexibility above all else.  That being said, CREAM is a little more verbose than other alternatives (although it's still not bad at all).

For each of your API calls (or whatever that's pulled externally that needs to be cached) you'll need to make a Loader.

###Adding CREAM to your project

To include this module in your project, go to the releases and download the lastest release .aar file.

Take that aar file, and place it in your projects `libs` folder.

Add in to your projects `build.gradle` file's dependencies:

```gradle
dependencies {
    //...
    compile(name:'cream-v2.X.X', ext:'aar') // Replace X with actual version number
}
```

Also add the libs folder as a repository if you haven't already:

```gradle
repositories{
    flatDir{
        dirs 'libs'
    }
}
```

### Example

To see an example, visit https://github.com/carrot/cream-example, although much of the example is discussed in this README.

###LoaderParams - Setup (Loader Param)

LoaderParams are used to pass required parameters to the loaders.

```java

public class GithubUserLoaderParams implements LoaderParams
{
    // ...

    /**
    * The most important method.
    * @return A value that uniquely identifies an api request.
    */
    @Override
    public String getIdentifier()
    {
        return getUserId();
    }
}
```

###SingleLoader - Setup (Single API Param)

Single loaders are the bread and butter of CREAM.  They're used directly to make a single cached external call, and are very simply passed into RetryLoaders and MultipleLoaders to get them up and running really quickly.

```java
public class GithubUserLoader extends SingleLoader<GithubUserLoaderParams, GithubUser> {

    public GithubUserLoader(Context context, CacheStrategy<GithubUserLoaderParams, GithubUser> cacheStrategy) {
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
    public boolean shouldCache(GithubUserLoaderParams user) {
        return true;
    }

    /**
     * In this function we retrieve what we want to cache,
     * here I'm using retrofit -- but you can use whatever you want
     * as long as you can pack the result into a serializable object.
     */
    @Override
    protected void loadFromSource(final GithubUserLoaderParams param, final SingleLoaderCallback singleLoaderCallback) {

        final SingleLoader<GithubUserLoaderParams, GithubUser> thisLoader = this;

        GithubAPIBuilder.getAPI().getUser(param.getUserId(), new Callback<GithubUser>() {
            @Override
            public void success(GithubUser githubUser, Response response) {
                mCacheStrategy.handleSourceSuccess(param, githubUser, thisLoader, singleLoaderCallback);
            }

            @Override
            public void failure(RetrofitError error) {
                mCacheStrategy.handleSourceFailure(param, error, thisLoader, singleLoaderCallback);
            }
        });
    }

}
```

###SingleLoader - Setup (Multiple API Params)

Most of your API calls are probably going to have more than one parameter.  You'll have to create a LoaderParams Object.

In the LoaderParams object, you'll need to implement the getIdentifier() method in a manner that uniquely identifies the API call.

```java
public class GithubRepoLoader extends DefaultLoader<GithubRepoLoader.RepoParams, GithubRepo>{

    public GithubRepoLoader(Context context, CacheStrategy<RepoParams, GithubRepo> cacheStrategy) {
        super(context, cacheStrategy);
    }

    @Override
    protected String getFileExtension() {
        return "repo";
    }

    @Override
    protected void loadFromSource(final RepoParams repoParams, final SingleLoaderCallback cb){
        final GithubRepoLoader thisLoader = this;

        GithubAPIBuilder.getAPI().getRepo(repoParams.owner, repoParams.name, new Callback<GithubRepo>() {
            @Override
            public void success(GithubRepo githubRepo, Response response) {
                mCacheStrategy.handleSourceSuccess(repoParams, githubRepo, thisLoader, cb);
            }

            @Override
            public void failure(RetrofitError error) {
                mCacheStrategy.handleSourceFailure(repoParams, error, thisLoader, cb);
            }
        });
    }

    /**
     * See "Multiple params in API Call"
     * section in GithubRepoLoader
     */
    public class RepoParams implements LoaderParams
    {
        public String owner;
        public String name;

        @Override
        public String getIdentifier()
        {
            return owner + "." + name;
        }
    }

}

```

###SingleLoader - Usage

```java
// Getting the userName from the field
String userName = "BrandonRomano";

//Creating a StandardCacheStrategy object to plug into the Loader
CacheStrategy<GithubUserLoaderParams, GithubUser> cacheStrategy = new CachePreferred<GithubUserLoaderParams, GithubUser>(this);

// Creating the loader + calling loadSelf
GithubUserLoader loader = new GithubUserLoader(this, cacheStrategy);
loader.loadSelf(new GithubUserLoaderParams(userName), new SingleLoaderCallback<GithubUser>() {
    @Override
    public void success(GithubUser user, boolean fromCache) {
        // Success!  We have the user here, do whatever you please to them.
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
ArrayList<GithubUserLoaderParams> paramsList = new ArrayList<GithubUserLoaderParams>();
paramsList.add( new GithubUserLoaderParams("BrandonRomano");
paramsList.add( new GithubUserLoaderParams("roideuniverse");

//Creating a StandardCacheStrategy object to plug into the Loader
CacheStrategy<GithubUserLoaderParams, GithubUser> cacheStrategy = new CachePreferred<GithubUserLoaderParams, GithubUser>(this);

// Creating the single loader
final GithubUserLoader singleLoader = new GithubUserLoader(getContext(), cacheStrategy);

// Creating a multiple loader with a STRICT_POLICY (all successful or loader will fail)
MultipleLoader<GithubUserLoaderParams, GithubUser> multipleLoader = new MultipleLoader<GithubUserLoaderParams, GithubUser>(MultipleLoader.STRICT_POLICY);

// Load!
multipleLoader.load(paramsList, singleLoader, new MultipleLoaderCallback() {
    @Override
    public void success(ArrayList<MultipleLoaderTuple<GithubUser>> loaderTuples) {
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
// Creating a CachePreferred object to plug into the Loader
CacheStrategy<GithubUserLoaderParams, GithubUser> cacheStrategy = new CachePreferred<GithubUserLoaderParams, GithubUser>(this);

// Creating the loader
final GithubUserLoader singleLoader = new GithubUserLoader(getContext(), cacheStrategy);

//create the params
GithubUserLoaderParams params = new GithubUserLoaderParams("BrandonRomano");

// Creating a retry loader
final RetrySingleLoader<GithubUserLoaderParams, GithubUser> retrySingleLoader = new RetrySingleLoader<GithubUserLoaderParams, GithubUser>(singleLoader);

// Load!
retrySingleLoader.loadSelf(params, new RetrySingleLoaderCallback<GithubUser>() {
    @Override
    public void success(GithubUser user, boolean fromCache) {
        // Success!  We have the user here, do whatever you please to them.
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
// Creating an ArrayList of all of the users we will download
ArrayList<GithubUserLoaderParams> paramsList = new ArrayList<GithubUserLoaderParams>();
paramsList.add( new GithubUserLoaderParams("BrandonRomano");
paramsList.add( new GithubUserLoaderParams("roideuniverse");

// Creating a CachePreferred object to plug into the Loader
CacheStrategy<GithubUserLoaderParams, GithubUser> cacheStrategy = new CachePreferred<GithubUserLoaderParams, GithubUser>(getContext());

// Creating the loader
final GithubUserLoader singleLoader = new GithubUserLoader(getContext(), cacheStrategy);

// Creating a multiple loader with a STRICT_POLICY
final MultipleLoader<GithubUserLoaderParams, GithubUser> multipleLoader = new MultipleLoader<GithubUserLoaderParams, GithubUser>(MultipleLoader.STRICT_POLICY);

// Creating a retryMultipleLoader
final RetryMultipleLoader<GithubUserLoaderParams, GithubUser> retryMultipleLoader = new RetryMultipleLoader<GithubUserLoaderParams, GithubUser>(multipleLoader, singleLoader);

// Load!
retryMultipleLoader.loadSelf(paramsList, new RetryMultipleLoaderCallback() {
    @Override
    public void success(ArrayList<MultipleLoaderTuple<GithubUser>> loaderTuples) {
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

There's an example that goes over most of the features, and can be found [here](https://github.com/carrot/cream-example).
