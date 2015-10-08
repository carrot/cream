package com.carrotcreative.cream.loaders.retry.multiple;

import com.carrotcreative.cream.loaders.multiple.MultipleLoaderTuple;

import java.io.Serializable;
import java.util.ArrayList;

public interface RetryMultipleLoaderCallback<Content extends Serializable> {
    void success(ArrayList<MultipleLoaderTuple<Content>> content);
    void failedAttempt(int attemptNumber);
    void always();
}