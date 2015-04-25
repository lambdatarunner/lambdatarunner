package org.lambdatarunner.internal;

import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

public class IgnoredParameterizedFrameworkMethod extends FrameworkMethod {

    public IgnoredParameterizedFrameworkMethod(FrameworkMethod method) {
        super(method.getMethod());
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
}
