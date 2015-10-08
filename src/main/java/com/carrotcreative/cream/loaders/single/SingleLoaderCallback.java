package com.carrotcreative.cream.loaders.single;

import java.io.Serializable;

public interface SingleLoaderCallback<Content extends Serializable> {
    void success(Content content, boolean fromCache);
    void failure(Exception error);
    void always();
}