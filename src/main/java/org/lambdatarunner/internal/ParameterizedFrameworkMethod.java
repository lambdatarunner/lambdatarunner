package org.lambdatarunner.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.lambdatarunner.Datum;
import org.lambdatarunner.DescribeAs;
import org.lambdatarunner.TestSpec;

public class ParameterizedFrameworkMethod extends FrameworkMethod {

    private final TestSpec testSpec;

    private final Description description;

    private final Object test;

    /**
     * Create a new instance.
     * @param method the @Test-annotated method which returns
     * @param testSpec the test to run, with data bound
     * @param test the test object
     * @param count A unique number for this run of the test to help distinguish it from other runs.
     */
    public ParameterizedFrameworkMethod(Method method, TestSpec testSpec, Object test, int count) {
        super(method);
        this.testSpec = testSpec;
        this.test = test;
        description = Description.createTestDescription(
            test.getClass(), parameterizedTestName(method, testSpec.getDatum(), count), method.getAnnotations());
    }

    private String parameterizedTestName(Method method, Datum datum, int count) {
        DescribeAs describeAs = method.getAnnotation(DescribeAs.class);
        return (describeAs == null)
            ? method.getName() + ": " + count
            : method.getName() + ": " + MessageFormat.format(
                describeAs.value(),
                datum.values());

    }

    TestSpec getTestSpec() {
        return testSpec;
    }

    /**
     * Get the test instance used to run the test method
     * @return the test instance used to run the test method
     */
    public Object getTest() {
        return test;
    }

    @Override
    public void validatePublicVoid(boolean isStatic, List<Throwable> errors) {
        if (Modifier.isStatic(getMethod().getModifiers()) != isStatic) {
            String state= isStatic ? "should" : "should not";
            errors.add(new Exception("Method " + getMethod().getName() + "() " + state + " be static"));
        }
        if (!Modifier.isPublic(getMethod().getDeclaringClass().getModifiers()))
            errors.add(new Exception("Class " + getMethod().getDeclaringClass().getName() + " should be public"));
        if (!Modifier.isPublic(getMethod().getModifiers()))
            errors.add(new Exception("Method " + getMethod().getName() + "() should be public"));
    }

    @Override
    public String getName() {
        return description.getDisplayName();
    }

    public Description getDescription() {
        return description;
    }
}
