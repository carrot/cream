package com.carrotcreative.cream.loaders.multiple;

import java.io.Serializable;
import java.util.ArrayList;

public interface MultipleLoaderCallback<Content extends Serializable> {
    void success(ArrayList<MultipleLoaderTuple<Content>> content);
    void failure(Exception error);
    void always();
}