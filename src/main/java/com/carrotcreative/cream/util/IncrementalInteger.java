package com.carrotcreative.cream.util;

public class IncrementalInteger {

    private int mInt;

    public IncrementalInteger(int initialValue)
    {
        mInt = initialValue;
    }

    public void increment()
    {
        mInt++;
    }

    public int value()
    {
        return mInt;
    }

}