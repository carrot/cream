package com.carrotcreative.cream.loaders.single;

import java.io.Serializable;

public interface SingleLoaderCallback {
    void success(Serializable content, boolean fromCache);
    void failure(Exception error);
}