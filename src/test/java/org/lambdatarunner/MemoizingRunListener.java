package org.lambdatarunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class MemoizingRunListener extends RunListener {

    public static abstract class Event<T> {
        private final T eventData;

        protected Event(T eventData) {
            this.eventData = eventData;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null
                && obj.getClass().equals(getClass())
                && Objects.equals(eventData, ((Event<?>) obj).eventData);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(eventData);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + ": " + Objects.toString(eventData);
        }
    }

    private List<Event<?>> events = new ArrayList<>();

    public static class TestAssumptionFailure extends Event<FailureMirror> {
        protected TestAssumptionFailure(FailureMirror eventData) { super(eventData); }
    }

    public static class TestFailure extends Event<FailureMirror> {
        protected TestFailure(FailureMirror eventData) { super(eventData); }
    }

    public static class TestFinished extends Event<Description> {
        protected TestFinished(Description eventData) { super(eventData); }
    }

    public static class TestIgnored extends Event<Description> {
        protected TestIgnored(Description eventData) { super(eventData); }
    }

    public static class TestRunStarted extends Event<Description> {
        protected TestRunStarted(Description eventData) { super(eventData); }
    }

    public static class TestStarted extends Event<Description> {
        protected TestStarted(Description eventData) { super(eventData); }
    }

    public static class TestRunFinished extends Event<ResultMirror> {
        protected TestRunFinished(ResultMirror eventData) { super(eventData); }
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        record(new TestAssumptionFailure(new FailureMirror(failure)));
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        record(new TestFailure(new FailureMirror(failure)));
    }

    @Override
    public void testFinished(org.junit.runner.Description description) throws Exception {
        record(new TestFinished(description));
    };

    @Override
    public void testIgnored(Description description) throws Exception {
        record(new TestIgnored(description));
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        record(new TestRunFinished(new ResultMirror(result)));
    };

    @Override
    public void testRunStarted(Description description) throws Exception {
        record(new TestRunStarted(description));
    };

    @Override
    public void testStarted(Description description) throws Exception {
        record(new TestStarted(description));
    };

    public List<Event<?>> getEvents() {
        return events;
    }

    public void verifyEvents(Event<?>... expectedEvents) {
        Assert.assertEquals(Arrays.asList(expectedEvents), events);
    }

    private void record(Event<?> event) {
        events.add(event);
    }
}
