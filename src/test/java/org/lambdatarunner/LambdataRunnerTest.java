package org.lambdatarunner;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;

import static org.lambdatarunner.Lambdata.*;

public class LambdataRunnerTest {
    private final static AssertionError ASSERTION_ERROR1 = new AssertionError("fail1");
    private final static AssertionError ASSERTION_ERROR2 = new AssertionError("fail2");
    private final static Exception EXCEPTION1 = new Exception("random1");
    private final static Exception EXCEPTION2 = new Exception("random2");

    @RunWith(LambdataRunner.class)
    public static class SimpleTestSuccess {
        @Test public void testSucceed() { assertTrue(true); }
    }

    @Test
    public void simpleTestSuccessRun() throws Exception {
        Class<SimpleTestSuccess> testClass = SimpleTestSuccess.class;
        Description testDescription = Description.createTestDescription(testClass, "testSucceed");
        runTests(testClass).verifyEvents(
            testRunStarted(),
            testStarted(testDescription),
            testFinished(testDescription),
            testRunFinished(0, 1));
    }

    @RunWith(LambdataRunner.class)
    public static class SimpleTestFailure {
        @Test public void testFail() { throw ASSERTION_ERROR1; }
    }

    @Test
    public void simpleTestFailureRun() throws Exception {
        Class<SimpleTestFailure> testClass = SimpleTestFailure.class;
        Description testDescription = Description.createTestDescription(testClass, "testFail");
        runTests(testClass).verifyEvents(
            testRunStarted(),
            testStarted(testDescription),
            testFailure(testDescription, ASSERTION_ERROR1),
            testFinished(testDescription),
            testRunFinished(0, 1, new FailureMirror(testDescription, ASSERTION_ERROR1)));
    }

    @RunWith(LambdataRunner.class)
    public static class SimpleTestException {
        @Test public void testException() throws Exception { throw EXCEPTION1; }
    }

    @Test
    public void simpleTestException() throws Exception {
        Class<SimpleTestException> testClass = SimpleTestException.class;
        Description testDescription = Description.createTestDescription(testClass, "testException");
        runTests(testClass).verifyEvents(
            testRunStarted(),
            testStarted(testDescription),
            testFailure(testDescription, EXCEPTION1),
            testFinished(testDescription),
            testRunFinished(0, 1, new FailureMirror(testDescription, EXCEPTION1)));
    }

    @RunWith(LambdataRunner.class)
    public static class SimpleTestIgnore {
        @Test @Ignore public void testIgnored() { throw ASSERTION_ERROR1; }
    }

    @Test
    public void simpleTestIgnored() throws Exception {
        Class<SimpleTestIgnore> testClass = SimpleTestIgnore.class;
        Description testDescription = Description.createTestDescription(testClass, "testIgnored");
        runTests(testClass).verifyEvents(
                testRunStarted(),
                testIgnored(testDescription),
                testRunFinished(1, 0));
    }


    @RunWith(LambdataRunner.class)
    public static class SimpleTestBeforeAfter {
        private static boolean beforeRun = false;
        private static boolean afterRun = false;
        @Before public void before() {
            beforeRun = true;
        }
        @Test public void testRun() { }
        @After public void after() {
            afterRun = true;
        }
    }

    @Test
    public void simpleTestBeforeAfter() throws Exception {
        Class<SimpleTestBeforeAfter> testClass = SimpleTestBeforeAfter.class;
        Description testDescription = Description.createTestDescription(testClass, "testRun");
        runTests(testClass).verifyEvents(
                testRunStarted(),
                testStarted(testDescription),
                testFinished(testDescription),
                testRunFinished(0, 1));
        assertTrue(SimpleTestBeforeAfter.beforeRun);
        assertTrue(SimpleTestBeforeAfter.afterRun);
    }

    @RunWith(LambdataRunner.class)
    public static class ParameterizedTestSuccess {
        @Test public TestSpecs testSucceed() {
            return specs((Integer i) -> { assertTrue(true); },
                datum(1),
                datum(2));
        }
    }

    @Test
    public void parameterizedTestSuccessRun() throws Exception {
        Class<ParameterizedTestSuccess> testClass = ParameterizedTestSuccess.class;
        Description testDescription1 = Description.createTestDescription(testClass, "testSucceed: 1");
        Description testDescription2 = Description.createTestDescription(testClass, "testSucceed: 2");
        runTests(testClass).verifyEvents(
            testRunStarted(),
            testStarted(testDescription1),
            testFinished(testDescription1),
            testStarted(testDescription2),
            testFinished(testDescription2),
            testRunFinished(0, 2));
    }

    @RunWith(LambdataRunner.class)
    public static class ParameterizedTestFailure {
        @Test public TestSpecs testFail() {
            return specs((Integer i) -> {
                switch (i) {
                    case 1: throw ASSERTION_ERROR1;
                    case 2: throw ASSERTION_ERROR2;
                    default: throw new RuntimeException();
                }
            },
            datum(1),
            datum(2));
        }
    }

    @Test
    public void parameterizedTestFailureRun() throws Exception {
        Class<ParameterizedTestFailure> testClass = ParameterizedTestFailure.class;
        Description testDescription1 = Description.createTestDescription(testClass, "testFail: 1");
        Description testDescription2 = Description.createTestDescription(testClass, "testFail: 2");
        runTests(testClass).verifyEvents(
            testRunStarted(),
            testStarted(testDescription1),
            testFailure(testDescription1, ASSERTION_ERROR1),
            testFinished(testDescription1),
            testStarted(testDescription2),
            testFailure(testDescription2, ASSERTION_ERROR2),
            testFinished(testDescription2),
            testRunFinished(0, 2,
                new FailureMirror(testDescription1, ASSERTION_ERROR1),
                new FailureMirror(testDescription2, ASSERTION_ERROR2)));
    }

    @RunWith(LambdataRunner.class)
    public static class ParameterizedTestException {
        @Test
        public TestSpecs testException() {
            return specs((Integer i) -> {
                switch (i) {
                    case 1: throw EXCEPTION1;
                    case 2: throw EXCEPTION2;
                    default: throw new RuntimeException();
                }
            },
            datum(1),
            datum(2));
        }
    }

    @Test
    public void parameterizedTestException() throws Exception {
        Class<ParameterizedTestException> testClass = ParameterizedTestException.class;
        Description testDescription1 = Description.createTestDescription(testClass, "testException: 1");
        Description testDescription2 = Description.createTestDescription(testClass, "testException: 2");
        runTests(testClass).verifyEvents(
            testRunStarted(),
            testStarted(testDescription1),
            testFailure(testDescription1, EXCEPTION1),
            testFinished(testDescription1),
            testStarted(testDescription2),
            testFailure(testDescription2, EXCEPTION2),
            testFinished(testDescription2),
            testRunFinished(0, 2,
                new FailureMirror(testDescription1, EXCEPTION1),
                new FailureMirror(testDescription2, EXCEPTION2)));
    }

    @RunWith(LambdataRunner.class)
    public static class ParameterizedTestIgnore {
        @Test @Ignore public TestSpecs testIgnored() throws Exception {
            throw EXCEPTION1;
        }
    }

    @Test
    public void paramterizedTestIgnored() throws Exception {
        Class<ParameterizedTestIgnore> testClass = ParameterizedTestIgnore.class;
        Description testDescription = Description.createTestDescription(testClass, "testIgnored");
        runTests(testClass).verifyEvents(
            testRunStarted(),
            testIgnored(testDescription),
            testRunFinished(1, 0));
    }

    @RunWith(LambdataRunner.class)
    public static class ParameterizedTestSuccessBeforeAfter {
        static int instantiationCount = 0;

        public ParameterizedTestSuccessBeforeAfter() {
            instantiationCount++;
        }

        boolean initialized = false;
        @Before public void before() {
            if (initialized) { throw new RuntimeException(); }
            initialized = true;
        }
        @After public void after() { initialized = false; }
        @Test public TestSpecs testSucceed() {
            return specs((Integer i) -> { assertTrue(initialized); },
                    datum(1),
                    datum(2));
        }
    }

    @Test
    public void parameterizedTestSuccessBeforeAfterRun() throws Exception {
        Class<ParameterizedTestSuccessBeforeAfter> testClass = ParameterizedTestSuccessBeforeAfter.class;
        Description testDescription1 = Description.createTestDescription(testClass, "testSucceed: 1");
        Description testDescription2 = Description.createTestDescription(testClass, "testSucceed: 2");
        runTests(testClass).verifyEvents(
                testRunStarted(),
                testStarted(testDescription1),
                testFinished(testDescription1),
                testStarted(testDescription2),
                testFinished(testDescription2),
                testRunFinished(0, 2));
        // all runs should take place on original instance
        assertEquals(1, ParameterizedTestSuccessBeforeAfter.instantiationCount);
    }

    @RunWith(LambdataRunner.class)
    public static class ParameterizedTestExceptionOnDataCreation {
        @Test public TestSpecs testCreationFails() throws Exception {
            throw EXCEPTION1;
        }
    }

    @Test
    public void exceptionInDatumCreation() throws Exception {
        Class<ParameterizedTestExceptionOnDataCreation> testClass = ParameterizedTestExceptionOnDataCreation.class;
        Description testDescription = Description.createTestDescription(
            testClass, "initializationError");
        runTests(testClass).verifyEvents(
            testRunStarted(),
            testStarted(testDescription),
            testFailure(testDescription, new RuntimeException(EXCEPTION1)),
            testFinished(testDescription),
            testRunFinished(0, 1, new FailureMirror(testDescription, new RuntimeException(EXCEPTION1))));
    }

    @RunWith(LambdataRunner.class)
    public static class ParameterizedTestNoData {
        @Test public void testSucceed() {
            assertTrue(true);
        }

        @Test public TestSpecs testNoData() {
            return specs((Integer i) -> { assertTrue(true); });
        }
    }

    @Test
    public void testNoData() throws Exception {
        Class<ParameterizedTestNoData> testClass = ParameterizedTestNoData.class;
        Description testDescription = Description.createTestDescription(testClass, "testSucceed");
        runTests(testClass).verifyEvents(
            testRunStarted(),
            testStarted(testDescription),
            testFinished(testDescription),
            testRunFinished(0, 1));
    }

    @RunWith(LambdataRunner.class)
    public static class ParameterizedTestCustomLabel {
        @DescribeAs("length of ''{0}''")
        @Test public TestSpecs testLength() {
            return specs(
                (string, length) -> assertEquals((int) length, string.length()),
                datum("hello", 5),
                datum("a", 1));
        }

        @Test public TestSpecs testNoData() {
            return specs((Integer i) -> { assertTrue(true); });
        }
    }

    @Test
    public void testCustomLabel() throws Exception {
        Class<ParameterizedTestCustomLabel> testClass = ParameterizedTestCustomLabel.class;
        Description testDescription1 = Description.createTestDescription(testClass, "testLength: length of 'hello'");
        Description testDescription2 = Description.createTestDescription(testClass, "testLength: length of 'a'");
        runTests(testClass).verifyEvents(
            testRunStarted(),
            testStarted(testDescription1),
            testFinished(testDescription1),
            testStarted(testDescription2),
            testFinished(testDescription2),
            testRunFinished(0, 2));
    }

    private MemoizingRunListener runTests(Class<?> testClass) throws Exception {
        MemoizingRunListener runListener = new MemoizingRunListener();
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(runListener);
        jUnitCore.run(testClass);
        return runListener;
    }

    private MemoizingRunListener.TestRunStarted testRunStarted() {
        return new MemoizingRunListener.TestRunStarted(Description.createSuiteDescription("null"));
    }

    private MemoizingRunListener.TestStarted testStarted(Description testDescription) {
        return new MemoizingRunListener.TestStarted(testDescription);
    }

    private MemoizingRunListener.TestFailure testFailure(Description testDescription, Throwable exception) {
        return new MemoizingRunListener.TestFailure(new FailureMirror(testDescription, exception));
    }

    private MemoizingRunListener.TestFinished testFinished(Description testDescription) {
        return new MemoizingRunListener.TestFinished(testDescription);
    }

    private MemoizingRunListener.TestIgnored testIgnored(Description description) {
        return new MemoizingRunListener.TestIgnored(description);
    }

    private MemoizingRunListener.TestRunFinished testRunFinished(int ignoreCount, int runCount, FailureMirror... failures) {
        return new MemoizingRunListener.TestRunFinished(new ResultMirror(Arrays.asList(failures), ignoreCount, runCount));
    }
}
