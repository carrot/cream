package com.carrotcreative.cream_example.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.carrotcreative.cream.loaders.retry.single.RetrySingleLoader;
import com.carrotcreative.cream.loaders.retry.single.RetrySingleLoaderCallback;
import com.carrotcreative.cream.strategies.generic.CacheStrategy;
import com.carrotcreative.cream.strategies.CachePreferred;
import com.carrotcreative.cream_example.app.R;
import com.carrotcreative.cream_example.app.cache.loaders.GithubUserLoader;
import com.carrotcreative.cream_example.app.net.GithubUser;
import com.carrotcreative.cream_example.app.util.DisplayManager;

import java.io.Serializable;

/**
 * The retry loader is a nice way to keep trying given that API + Cache has failed.
 * You can control how many times you would like to retry, as you're the one in
 * control of calling RetryLoaer.retry() in the event of a failed attempt
 */
public class RetryLoaderActivity extends Activity implements RetrySingleLoaderCallback {

    private boolean mSubmitLock;
    private EditText mUsernameField;
    private Activity mThisActivity;
    private RetrySingleLoader mRetryLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_loader);
        mSubmitLock = false;
        mThisActivity = this;
        prepareViews();
    }

    private void prepareViews()
    {
        mUsernameField = (EditText) findViewById(R.id.username_field);

        findViewById(R.id.username_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mSubmitLock)
                    handleSubmit();
            }
        });
    }

    private void handleSubmit()
    {
        mSubmitLock = true;

        // Getting the userName from the field
        String userName = mUsernameField.getText().toString();

        //Creating a StandardCacheStrategy object to plug into the Loader
        CacheStrategy<String> cacheStrategy = new CachePreferred<String>(this);

        // Creating the loader + calling loadSelf
        GithubUserLoader loader = new GithubUserLoader(this, cacheStrategy);

        // Creating the Retry Loader wrapper
        mRetryLoader = new RetrySingleLoader<String>(loader);
        mRetryLoader.loadSelf(userName, this);
    }

    @Override
    public void success(Serializable serializable, boolean fromCache) {
        // Success!  We have the user here
        GithubUser user = (GithubUser) serializable;

        //Do whatever you want with it... Here we just display
        DisplayManager.displaySuccess(user, fromCache, mThisActivity);

        //Unlock the submit button
        mSubmitLock = false;
    }

    @Override
    public void failedAttempt(int i) {
        //Failure, handle this however you would like
        DisplayManager.displayAttempt(mThisActivity, i);

        //Continue to retry -- You can add specific constraints
        mRetryLoader.retry();
    }

    @Override
    public void always() {
        /* Do nothing */
    }

}
