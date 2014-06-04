package com.carrotcreative.cream.test;

import android.test.InstrumentationTestCase;

import com.carrotcreative.cream.loaders.multiple.MultipleLoader;
import com.carrotcreative.cream.loaders.multiple.MultipleLoaderCallback;
import com.carrotcreative.cream.loaders.multiple.MultipleLoaderTuple;
import com.carrotcreative.cream.loaders.retry.RetryLoader;
import com.carrotcreative.cream.loaders.retry.single.RetrySingleLoader;
import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;
import com.carrotcreative.cream.strategies.CacheStrategy;
import com.carrotcreative.cream.strategies.StandardCacheStrategy;
import com.carrotcreative.cream.test.util.AsyncFunctionFunctor;
import com.carrotcreative.cream.test.util.AsyncFunctionTest;
import com.carrotcreative.cream.test.util.ErrorHolder;
import com.carrotcreative.cream_example.app.cache.loaders.GithubUserLoader;
import com.carrotcreative.cream_example.app.net.GithubUser;

import java.io.Serializable;
import java.util.ArrayList;
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

    /**
     * Testing out a multiple loader
     */
    public void testMultipleLoader() throws Throwable {
        // Creating a StandardCacheStrategy object to plug into the Loader
        CacheStrategy<String> cacheStrategy = new StandardCacheStrategy<String>(getInstrumentation().getContext());

        // Creating the loader
        final GithubUserLoader singleLoader = new GithubUserLoader(getInstrumentation().getContext(), cacheStrategy);

        // Creating a multiple loader with a STRICT_POLICY
        final MultipleLoader<String> multipleLoader = new MultipleLoader<String>(MultipleLoader.STRICT_POLICY);

        // Creating an ArrayList of all of the users I would like to download
        final ArrayList<String> userNames = new ArrayList<String>();
        userNames.add("BrandonRomano");
        userNames.add("pruett");
        userNames.add("kylemac");
        userNames.add("nporteschaikin");

        // Testing + Running
        AsyncFunctionTest.test(this, 15, new AsyncFunctionFunctor() {
            @Override
            public void runAsync(final CountDownLatch signal, final ErrorHolder errorHolder) {
                multipleLoader.load(userNames, singleLoader, new MultipleLoaderCallback() {
                    @Override
                    public void success(ArrayList<MultipleLoaderTuple> loaderTuples) {
                        if(loaderTuples.size() != 4)
                        {
                            errorHolder.mHasError = true;
                            errorHolder.mErrorMessage = "Something is wrong with STRICT_POLICY";
                        }
                    }

                    @Override
                    public void failure(Exception e) {
                        errorHolder.mHasError = true;
                        errorHolder.mErrorMessage = "Failure to pull Multiple Loader.  Check your internet connection and verify that Github's API is up before debugging.";
                    }

                    @Override
                    public void always() {
                        signal.countDown();
                    }
                });
            }
        });
    }

    /**
     * Testing out a retry single loader
     */
    public void testRetrySingleLoader() throws Throwable {

    }

    /**
     * Testing out a retry multiple loader
     */
    public void testRetryMultipleLoader()
    {

    }

}