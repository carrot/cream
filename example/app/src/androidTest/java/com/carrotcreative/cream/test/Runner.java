package com.carrotcreative.cream.test;

import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;
import junit.framework.TestSuite;

public class Runner extends InstrumentationTestRunner {

    @Override
    public TestSuite getAllTests(){
        InstrumentationTestSuite suite = new InstrumentationTestSuite(this);
        suite.addTestSuite(LoaderTest.class);
        suite.addTest(new LoaderTest());
        return suite;
    }

    @Override
    public ClassLoader getLoader() {
        return Runner.class.getClassLoader();
    }

}