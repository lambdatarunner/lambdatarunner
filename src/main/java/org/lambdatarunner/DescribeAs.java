package org.lambdatarunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.MessageFormat;

/**
 * Provides a template for describing the individual runs of a parameterized test run by {@link LambdataRunner}.
 * The value provided by this annotation will be parsed by {@link MessageFormat#format(String, Object...)}; the
 * arguments to the format string will be the fields of the datum for the current test run.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DescribeAs {
    String value();
}
