package org.lambdatarunner.internal;

import org.junit.runners.model.Statement;
import org.lambdatarunner.TestSpec;

public class ParameterizedInvokeMethod extends Statement {

    private final TestSpec testSpec;

    public ParameterizedInvokeMethod(ParameterizedFrameworkMethod testMethod) {
        this.testSpec = testMethod.getTestSpec();
    }

    @Override
    public void evaluate() throws Throwable {
        testSpec.run();
    }

}
