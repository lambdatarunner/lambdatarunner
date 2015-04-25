package org.lambdatarunner;

import java.util.List;

public class TestSpecs {
    private final List<TestSpec> specs;

    public TestSpecs(List<TestSpec> specs) {
        this.specs = specs;
    }

    public List<TestSpec> getSpecs() {
        return specs;
    }
}
