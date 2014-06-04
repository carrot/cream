package com.carrotcreative.cream.test;

import android.test.InstrumentationTestCase;

import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;
import com.carrotcreative.cream.strategies.CacheStrategy;
import com.carrotcreative.cream.strategies.StandardCacheStrategy;
import com.carrotcreative.cream.test.util.ErrorHolder;
import com.carrotcreative.cream_example.app.cache.loaders.GithubUserLoader;
import com.carrotcreative.cream_example.app.net.GithubUser;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SingleLoaderTest extends InstrumentationTestCase {

    public SingleLoaderTest()
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

    public void testSingleLoader () throws Throwable {
        // create  a signal to let us know when our task is done.
        final CountDownLatch signal = new CountDownLatch(1);

        // Error holder so we can run this on the UI thread after the latch releases
        final ErrorHolder errorHolder = new ErrorHolder();
        errorHolder.mHasError = false;

        // Creating a StandardCacheStrategy object to plug into the Loader
        CacheStrategy<String> cacheStrategy = new StandardCacheStrategy<String>(getInstrumentation().getContext());

        // Creating the loader
        final GithubUserLoader loader = new GithubUserLoader(getInstrumentation().getContext(), cacheStrategy);

        // Execute the async task on the UI thread! THIS IS KEY!
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {

                loader.loadSelf("BrandonRomano", new SingleLoaderCallback() {
                    @Override
                    public void success(Serializable serializable, boolean b) {
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

        // Giving this 15 seconds before we time out
        signal.await(15, TimeUnit.SECONDS);

        if(errorHolder.mHasError) {
            fail(errorHolder.mErrorMessage);
        }
    }

}