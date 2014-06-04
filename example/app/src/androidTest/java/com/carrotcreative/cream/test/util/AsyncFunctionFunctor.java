package com.carrotcreative.cream.test.util;

import java.util.concurrent.CountDownLatch;

public interface AsyncFunctionFunctor {
    public void runAsync(CountDownLatch signal, ErrorHolder errorHolder);
}