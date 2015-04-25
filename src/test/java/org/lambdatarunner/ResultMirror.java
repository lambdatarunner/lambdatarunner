package org.lambdatarunner;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.runner.Result;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

/**
 * Mirror of {@link Result} which supports equals, toString and hashCode
 */
@AutoProperty
public class ResultMirror {
    final List<FailureMirror> failures;
    final int ignoreCount;
    final int runCount;

    public ResultMirror(Result result) {
        failures = result.getFailures().stream().map(FailureMirror::new).collect(Collectors.toList());
        ignoreCount = result.getIgnoreCount();
        runCount = result.getRunCount();
    }

    public ResultMirror(List<FailureMirror> failures, int ignoreCount, int runCount) {
        this.failures = failures;
        this.ignoreCount = ignoreCount;
        this.runCount = runCount;
    }

    @Override
    public String toString() {
        return Pojomatic.toString(this);
    }

    @Override
    public boolean equals(Object other) {
        return Pojomatic.equals(this, other);
    }

    @Override
    public int hashCode() {
        return Pojomatic.hashCode(this);
    }
}
