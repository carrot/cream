package com.carrotcreative.cream.loaders.retry.multiple;

import java.io.Serializable;
import java.util.ArrayList;

public interface RetryMultipleLoaderCallback<Content extends Serializable> {
    void success(ArrayList<Content> content);
    void failedAttempt(int attemptNumber);
    void always();
}