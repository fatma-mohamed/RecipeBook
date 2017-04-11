package com.example.android.recipebook.app;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by Fatma on 26-Jan-17.
 */

public class FullTestSuite extends TestSuite {
    public FullTestSuite()
    {
        super();
    }

    public Test suite()
    {
        return new TestSuiteBuilder(FullTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }

}
