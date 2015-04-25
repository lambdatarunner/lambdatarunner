package org.lambdatarunner;

import java.util.Objects;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.pojomatic.annotations.Property;

import com.google.common.base.Preconditions;

public class FailureMirror {
    @Property
    final Description description;
    final Throwable exception;

    public FailureMirror(Failure failure) {
        this(failure.getDescription(),  failure.getException());
    }

    public FailureMirror(Description description, Throwable exception) {
        this.description = Preconditions.checkNotNull(description);
        this.exception = Preconditions.checkNotNull(exception);
    }

    @Override
    public String toString() {
        return "FailureMirror{description: {" + description + "}, exception: {" + exception + "}}";
    }

    @Override
    public boolean equals(Object other) {
        return other != null
            && getClass().equals(other.getClass())
            && description.equals(((FailureMirror) other).description)
            && exceptionsEqual(exception, ((FailureMirror) other).exception);
    }

    private static boolean exceptionsEqual(Throwable exception1, Throwable exception2) {
        if (! exception1.getClass().equals(exception2.getClass())) {
            return false;
        }
        if (! Objects.equals(exception1.getMessage(), exception2.getMessage())) {
            return false;
        }
        if (exception1.getCause() == null) {
            return exception2.getCause() == null;
        }
        if (exception2.getCause() == null) {
            return false;
        }
        if (exception1.getCause() == exception1) {
            return exception2.getCause() == exception2;
        }
        return exceptionsEqual(exception1.getCause(), exception2.getCause());
    }

    @Override
    public int hashCode() {
        return 1; // no plan to put these into hashsets anyway...
    }
}
