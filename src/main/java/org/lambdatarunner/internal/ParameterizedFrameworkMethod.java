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

    public ParameterizedFrameworkMethod(Class<?> klass, Method method, TestSpec testSpec, int count) {
        super(method);
        this.testSpec = testSpec;
        description = Description.createTestDescription(
            klass, parameterizedTestName(method, testSpec.getDatum(), count), method.getAnnotations());
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
