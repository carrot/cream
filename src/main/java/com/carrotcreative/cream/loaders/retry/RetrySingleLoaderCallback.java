package com.carrotcreative.cream.loaders.retry;

import java.io.Serializable;

public interface RetrySingleLoaderCallback {
    void success(Serializable content, boolean fromCache);
    void failedAttempt(int attemptNumber);
}