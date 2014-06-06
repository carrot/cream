package com.carrotcreative.cream_example.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.carrotcreative.cream_example.app.R;
import com.carrotcreative.cream_example.app.cache.CacheClearTask;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyOnClicks();
        clearOldCache();
    }

    private void clearOldCache()
    {
        CacheClearTask cacheClearTask = new CacheClearTask(this);
        cacheClearTask.execute();
    }

    private void applyOnClicks()
    {
        // Single Loader
        findViewById(R.id.single_loader_example_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSingleLoaderExample();
            }
        });

        // Multiple Loader
        findViewById(R.id.multiple_loader_example_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMultipleLoaderExample();
            }
        });

        // Retry Loader
        findViewById(R.id.retry_loader_example_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRetryLoaderExample();
            }
        });
    }

    private void startSingleLoaderExample()
    {
        Intent i = new Intent(this, SingleLoaderActivity.class);
        startActivity(i);
    }

    private void startMultipleLoaderExample()
    {
        Intent i = new Intent(this, MultipleLoaderActivity.class);
        startActivity(i);
    }

    private void startRetryLoaderExample()
    {
        Intent i = new Intent(this, RetryLoaderActivity.class);
        startActivity(i);
    }

}