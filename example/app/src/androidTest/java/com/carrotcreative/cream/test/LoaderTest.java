package com.carrotcreative.cream.test;

import android.test.InstrumentationTestCase;

import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;
import com.carrotcreative.cream.strategies.CacheStrategy;
import com.carrotcreative.cream.strategies.StandardCacheStrategy;
import com.carrotcreative.cream.test.util.AsyncFunctionFunctor;
import com.carrotcreative.cream.test.util.AsyncFunctionTest;
import com.carrotcreative.cream.test.util.ErrorHolder;
import com.carrotcreative.cream_example.app.cache.loaders.GithubUserLoader;
import com.carrotcreative.cream_example.app.net.GithubUser;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

public class LoaderTest extends InstrumentationTestCase {

    public LoaderTest()
    {
        super();
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Testing out a single loader
     */
    public void testSingleLoader() throws Throwable {
        // Creating a StandardCacheStrategy object to plug into the Loader
        CacheStrategy<String> cacheStrategy = new StandardCacheStrategy<String>(getInstrumentation().getContext());

        // Creating the loader
        final GithubUserLoader loader = new GithubUserLoader(getInstrumentation().getContext(), cacheStrategy);

        // Testing + Running
        AsyncFunctionTest.test(this, 15, new AsyncFunctionFunctor() {
            @Override
            public void runAsync(final CountDownLatch signal, final ErrorHolder errorHolder) {

                loader.loadSelf("BrandonRomano", new SingleLoaderCallback() {
                    @Override
                    public void success(Serializable serializable, boolean loadedFromCache) {
                        try {
                            GithubUser user = (GithubUser) serializable;
                        }
                        catch(ClassCastException e)
                        {
                            errorHolder.mHasError = true;
                            errorHolder.mErrorMessage = "Could not create user from response.";
                        }
                    }

                    @Override
                    public void failure(Exception e) {
                        errorHolder.mHasError = true;
                        errorHolder.mErrorMessage = "Failure to pull Single Loader.  Check your internet connection and verify that Github's API is up before debugging.";
                    }

                    @Override
                    public void always() {
                        signal.countDown();
                    }
                });

            }
        });
    }

}