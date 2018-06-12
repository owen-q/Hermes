package org.lunker.proxy.util.lambda;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
@FunctionalInterface
public interface FunctionWithException<T, R, E extends Exception> {
    R apply(T t) throws E;
}
