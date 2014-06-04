package com.carrotcreative.cream.loaders.retry;

import android.os.Handler;

public abstract class RetryLoader {

    protected static final int START_RETRY_SECONDS = 2;
    protected static final int MAX_RETRY_SECONDS = 64;
    protected static final float RETRY_SECONDS_GROWTH_RATE = 1.5f;

    protected int mAttemptNumber = 0;
    protected int mRetrySeconds;

    public RetryLoader()
    {
        mRetrySeconds = START_RETRY_SECONDS;
    }

    public void retry()
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                retryLoad();
                updateRetrySeconds();
            }
        }, mRetrySeconds * 1000);
    }

    private void updateRetrySeconds()
    {
        mRetrySeconds = Math.min(MAX_RETRY_SECONDS,
                (int) (mRetrySeconds * RETRY_SECONDS_GROWTH_RATE));
    }

    protected abstract void retryLoad();

}