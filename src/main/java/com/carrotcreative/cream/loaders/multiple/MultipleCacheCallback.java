package com.carrotcreative.cream.loaders.multiple;

import java.util.ArrayList;

public interface MultipleCacheCallback {
    void success(ArrayList<MultipleLoaderTuple> content);
    void failure(Exception error);
}