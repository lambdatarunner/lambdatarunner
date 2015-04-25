package org.lambdatarunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.lambdatarunner.internal.IgnoredParameterizedFrameworkMethod;
import org.lambdatarunner.internal.ParameterizedFrameworkMethod;
import org.lambdatarunner.internal.ParameterizedInvokeMethod;

/**
 * A JUnit runner which allows using lambdas to set up data-driven tests. To use, first, annotate your test class
 * with {@code @RunWith(LambdataRunner.class)}. To make a test method parameter-driven, annotate it as usual
 * with {@code @Test}, but declare the method to return an instance of type {@link TestSpecs}. Such instances are
 * created by calling the {@code specs} method on {@link Lambdata}, providing a lambda which specifies the test run,
 * followed a series of datum (created by the {@code datum} method on {@code LambdataRunner}) which will be provided
 * to the lambda in turn. The specs and datum methods have overloads allowing for the number of parameters to range
 * anywhere between 1 and 10.
 * <p >
 * When tests are run, they will be grouped by method. By default, each individual test run will have the name of the
 * method, followed by a counter that starts at one and increments for each datum. The counter can be replaced
 * by custom text supplied via the &#64;{@link DescribeAs} annotation, providing a format string suitable for
 * {@link MessageFormat#format(String, Object...)}. The arguments to the format string will be the fields of the
 * datum under test.
 * <p>
 * For example:
 * <code>
 * <pre>
 *   import static org.lambdatarunner.Lambdata.*;
 *   import org.lambdatarunner.LambdataRunner;
 *   import org.lambdatarunner.TestSpecs;
 *   import org.junit.runner.RunWith;
 *   import static org.junit.Assert.assertEquals;
 *   import org.junit.Test;
 *
 *   &#64;RunWith(LambdataRunner.class)
 *   public class MathTests {
 *     &#64;Test
 *     &#64;DescribeAs("for string ''{0}''")
 *     public TestSpecs testStringLength() {
 *       return specs(
 *         (string, length) -> assertEquals((int) length, string.length()),
 *         datum("", 0),
 *         datum("a", 1),
 *         datum("hello", 5));
 *     }
 *   }
 * </pre>
 * <code>
 */
public class LambdataRunner extends BlockJUnit4ClassRunner {
    private Map<Method, List<FrameworkMethod>> testMethods;

    public LambdataRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return getTestMethods().values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private Map<Method, List<FrameworkMethod>> getTestMethods() {
        if (testMethods == null) {
            testMethods = createTestMethods();
        }
        return testMethods;
    }

    private Map<Method, List<FrameworkMethod>> createTestMethods() {
        Map<Method, List<FrameworkMethod>> testMethods = new LinkedHashMap<>();
        for (FrameworkMethod candidate: super.computeTestMethods()) {
            if (isParameterizedMethod(candidate.getMethod())) {
                if (candidate.getAnnotation(Ignore.class) == null) {
                    testMethods.put(candidate.getMethod(), parameterizeMethod(candidate));
                }
                else {
                    testMethods.put(candidate.getMethod(), Collections.singletonList(new IgnoredParameterizedFrameworkMethod(candidate)));
                }
            }
            else {
                testMethods.put(candidate.getMethod(), Collections.singletonList(candidate));
            }
        }
        return testMethods;
    }

    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        computeTestMethods().forEach(testMethod -> testMethod.validatePublicVoid(false, errors));
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        return (method instanceof ParameterizedFrameworkMethod)
            ? new ParameterizedInvokeMethod((ParameterizedFrameworkMethod) method)
            : super.methodInvoker(method, test);
    }

    @Override
    public Description getDescription() {
        Description description = Description.createSuiteDescription(getName(), getTestClass().getAnnotations());
        for (Map.Entry<Method, List<FrameworkMethod>> entry: getTestMethods().entrySet()) {
            Method method = entry.getKey();
            if (isParameterizedMethod(method) && ! method.isAnnotationPresent(Ignore.class)) {
                Description testDescription =
                    Description.createSuiteDescription(method.getName(), method.getAnnotations());
                entry.getValue().stream()
                    .map(ParameterizedFrameworkMethod.class::cast)
                    .map(ParameterizedFrameworkMethod::getDescription)
                    .forEach(testDescription::addChild);
                description.addChild(testDescription);
            }
            else {
                description.addChild(describeChild(entry.getValue().get(0)));
            }
        }

        return description;
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        if (method instanceof ParameterizedFrameworkMethod) {
            return ((ParameterizedFrameworkMethod) method).getDescription();
        }
        else {
            return super.describeChild(method);
        }
    }

    private static boolean isParameterizedMethod(Method method) {
        return TestSpecs.class.isAssignableFrom(method.getReturnType());
    }

    private List<FrameworkMethod> parameterizeMethod(FrameworkMethod method) {
        AtomicInteger count = new AtomicInteger();
        return getTestSpecs(method).stream()
            .map(spec -> new ParameterizedFrameworkMethod(getTestClass().getJavaClass(), method.getMethod(), spec, count.incrementAndGet()))
            .collect(Collectors.toList());
    }

    private List<TestSpec> getTestSpecs(FrameworkMethod method) {
        try {
            return ((TestSpecs) method.getMethod().invoke(createTest())).getSpecs();
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            else {
                throw new RuntimeException(e.getCause());
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
