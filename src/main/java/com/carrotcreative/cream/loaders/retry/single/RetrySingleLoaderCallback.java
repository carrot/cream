package com.carrotcreative.cream.loaders.retry.single;

import java.io.Serializable;

public interface RetrySingleLoaderCallback<Content extends Serializable> {
    void success(Content content, boolean fromCache);
    void failedAttempt(int attemptNumber);
    void always();
}