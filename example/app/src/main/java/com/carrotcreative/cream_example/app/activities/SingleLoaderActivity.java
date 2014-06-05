package com.carrotcreative.cream_example.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.carrotcreative.cream.loaders.single.SingleLoaderCallback;
import com.carrotcreative.cream.strategies.generic.CacheStrategy;
import com.carrotcreative.cream.strategies.CachePreferred;
import com.carrotcreative.cream_example.app.R;
import com.carrotcreative.cream_example.app.cache.loaders.GithubUserLoader;
import com.carrotcreative.cream_example.app.net.GithubUser;
import com.carrotcreative.cream_example.app.util.DisplayManager;

import java.io.Serializable;

/**
 * Single Loaders are the bread and butter of CREAM.
 *
 * This allows you to make a single external call to an
 * API (or any other external source for that matter)
 * and the SingleLoader (defined earlier in cache.loaders)
 * will take care of the rest.
 */
public class SingleLoaderActivity extends Activity implements SingleLoaderCallback {

    private boolean mSubmitLock;
    private EditText mUsernameField;
    private Activity mThisActivity;

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
        loader.loadSelf(userName, this);
    }

    @Override
    public void success(Serializable serializable, boolean fromCache) {
        // Success!  We have the user here
        GithubUser user = (GithubUser) serializable;

        //Do whatever you want with it... Here we just display
        DisplayManager.displaySuccess(user, fromCache, mThisActivity);
    }

    @Override
    public void failure(Exception e) {
        //Failure, handle this however you would like
        DisplayManager.displayFailure(mThisActivity);
    }

    @Override
    public void always() {
        //Unlock the submit button
        mSubmitLock = false;
    }

}