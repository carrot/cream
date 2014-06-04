package com.carrotcreative.cream.loaders.multiple;

import java.util.ArrayList;

public interface MultipleLoaderCallback {
    void success(ArrayList<MultipleLoaderTuple> content);
    void failure(Exception error);
    void always();
}