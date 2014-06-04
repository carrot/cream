package com.carrotcreative.cream_example.app.util;

import android.app.Activity;
import android.widget.Toast;

import com.carrotcreative.cream.loaders.multiple.MultipleLoaderTuple;
import com.carrotcreative.cream_example.app.net.GithubUser;

import java.util.ArrayList;
import java.util.Iterator;

public class DisplayManager {

    public static void displaySuccess(GithubUser user, boolean fromCache, final Activity activity)
    {
        //Preparing message
        String message = "User ID - " + user.id;
        if(fromCache)
            message += " : Pulled from Cache";
        else
            message += " : Pulled from API";
        final String finalMessage = message;

        // Displaying output
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, finalMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void displayFailure(final Activity activity)
    {
        // Failure, display failure
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = "Failure to download user -- User probably doesn't exist.";
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void displayAttempt(final Activity activity, final int attempt)
    {
        // Failure, display failure
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = "Failure to download user, attempt " + attempt + ".  Trying again...";
                Toast.makeText(activity, message ,Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void displayMultipleSuccess(ArrayList<MultipleLoaderTuple> loaderTuples, final Activity activity)
    {
        String displayString = "Success downloading users -- ID's: ";

        Iterator<MultipleLoaderTuple> it = loaderTuples.iterator();
        while(it.hasNext())
        {
            MultipleLoaderTuple tuple = it.next();
            GithubUser user = (GithubUser) tuple.mContent;
            displayString += user.id;

            if(tuple.mFromCache)
                displayString += "(Cache)";
            else
                displayString += "(API)";

            if(it.hasNext())
            {
                displayString += ", ";
            }
        }

        final String finalString = displayString;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, finalString ,Toast.LENGTH_LONG).show();
            }
        });
    }

}