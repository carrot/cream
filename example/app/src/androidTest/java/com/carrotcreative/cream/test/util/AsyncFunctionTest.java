package com.carrotcreative.cream.test.util;

import android.test.InstrumentationTestCase;

import com.carrotcreative.cream.strategies.generic.CacheStrategy;
import com.carrotcreative.cream.strategies.CachePreferred;
import com.carrotcreative.cream_example.app.cache.loaders.GithubUserLoader;

import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AsyncFunctionTest {

    public static void test(InstrumentationTestCase testCase, int timeoutSeconds, final AsyncFunctionFunctor functor) throws Throwable {
        // create  a signal to let us know when our task is done.
        final CountDownLatch signal = new CountDownLatch(1);

        // Error holder so we can run this on the UI thread after the latch releases
        final ErrorHolder errorHolder = new ErrorHolder();
        errorHolder.mHasError = false;

        // Creating a StandardCacheStrategy object to plug into the Loader
        CacheStrategy<String> cacheStrategy = new CachePreferred<String>(testCase.getInstrumentation().getContext());

        // Creating the loader
        final GithubUserLoader loader = new GithubUserLoader(testCase.getInstrumentation().getContext(), cacheStrategy);

        // Execute the async task on the UI thread
        testCase.runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                functor.runAsync(signal, errorHolder);
            }
        });

        // Giving this timeoutSeconds seconds before we time out
        signal.await(timeoutSeconds, TimeUnit.SECONDS);

        if(errorHolder.mHasError) {
            TestCase.fail(errorHolder.mErrorMessage);
        }
    }

}