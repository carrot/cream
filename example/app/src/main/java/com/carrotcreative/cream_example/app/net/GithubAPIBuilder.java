package com.carrotcreative.cream_example.app.net;

import retrofit.RestAdapter;

public class GithubAPIBuilder {

    private static GithubAPI mGithubAPIInstance;

    public static GithubAPI getAPI()
    {
        if(mGithubAPIInstance == null)
        {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(GithubAPI.API_BASE_URL)
                    .build();

            mGithubAPIInstance = restAdapter.create(GithubAPI.class);
        }

        return mGithubAPIInstance;
    }

}