package org.owen.hermes.util.lambda;

/**
 * Created by owen_q on 2018. 4. 26..
 */
@FunctionalInterface
public interface FunctionWithException<T, R, E extends Exception> {
    R apply(T t) throws E;
}
