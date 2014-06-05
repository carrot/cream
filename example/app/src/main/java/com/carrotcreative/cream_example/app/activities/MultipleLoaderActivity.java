package com.carrotcreative.cream_example.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.carrotcreative.cream.loaders.multiple.MultipleLoader;
import com.carrotcreative.cream.loaders.multiple.MultipleLoaderCallback;
import com.carrotcreative.cream.loaders.multiple.MultipleLoaderTuple;
import com.carrotcreative.cream.strategies.CachePreferred;
import com.carrotcreative.cream.strategies.generic.CacheStrategy;
import com.carrotcreative.cream_example.app.R;
import com.carrotcreative.cream_example.app.cache.loaders.GithubUserLoader;
import com.carrotcreative.cream_example.app.util.DisplayManager;

import java.util.ArrayList;

/**
 * Multiple loader allows you to spawn multiple loaders, given multiple identifiers.
 * All of their callbacks are wrapped into one callback for ease of use.
 */
public class MultipleLoaderActivity extends Activity implements MultipleLoaderCallback{

    private boolean mSubmitLock;
    private EditText mUsernameField1;
    private EditText mUsernameField2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_loader);
        mSubmitLock = false;
        prepareViews();
    }

    private void prepareViews()
    {
        mUsernameField1 = (EditText) findViewById(R.id.username_one_field);
        mUsernameField2 = (EditText) findViewById(R.id.username_two_field);

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
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(mUsernameField1.getText().toString());
        ids.add(mUsernameField2.getText().toString());

        //Creating a StandardCacheStrategy object to plug into the Loader
        CacheStrategy<String> cacheStrategy = new CachePreferred<String>(this);

        // Creating the loader + calling loadSelf
        GithubUserLoader loader = new GithubUserLoader(this, cacheStrategy);

        // Creating a multiple loader with STRICT_POLICY, which succeeds only if all downloads succeed
        // MultipleLoader.RELAXED_POLICY will succeed if at least one succeeds.
        MultipleLoader<String> multipleLoader = new MultipleLoader<String>(MultipleLoader.STRICT_POLICY);

        //Calling load
        multipleLoader.load(ids, loader, this);
    }

    @Override
    public void success(ArrayList<MultipleLoaderTuple> loaderTuples) {
        // Displaying success, you can handle the tuples however you would like
        // Inside this tuple, mContent is the serializable object defined in the loader.
        DisplayManager.displayMultipleSuccess(loaderTuples, this);
    }

    @Override
    public void failure(Exception e) {
        //Failure, handle this however you would like
        DisplayManager.displayFailure(this);
    }

    @Override
    public void always() {
        //Unlock the submit button
        mSubmitLock = false;
    }

}