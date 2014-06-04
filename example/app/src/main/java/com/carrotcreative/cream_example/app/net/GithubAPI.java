package com.carrotcreative.cream_example.app.net;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface GithubAPI {

    public static final String API_BASE_URL = "https://api.github.com";

    @GET("/users/{username}")
    public void getUser(
            @Path("username") String username,
            Callback<GithubUser> cb
    );

}