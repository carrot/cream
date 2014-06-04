package com.carrotcreative.cream.loaders.retry.multiple;

import com.carrotcreative.cream.loaders.multiple.MultipleLoaderTuple;

import java.util.ArrayList;

public interface RetryMultipleLoaderCallback {
    void success(ArrayList<MultipleLoaderTuple> content);
    void failedAttempt(int attemptNumber);
    void always();
}