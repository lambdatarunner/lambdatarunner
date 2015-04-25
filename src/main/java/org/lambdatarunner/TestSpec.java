package org.lambdatarunner;


public interface TestSpec {
    void run() throws Throwable;

    Datum getDatum();
}
