package com.carrotcreative.cream_example.app.cache;

import android.content.Context;
import android.os.AsyncTask;

import com.carrotcreative.cream_example.app.cache.loaders.GithubUserLoader;

public class CacheClearTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;

    public CacheClearTask(Context context)
    {
        super();
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        new GithubUserLoader(mContext, null).runCleanup();
        return null;
    }

}