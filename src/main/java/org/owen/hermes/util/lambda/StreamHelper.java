package org.owen.hermes.util.lambda;

import java.util.function.Function;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public class StreamHelper {


    public static <T, R, E extends Exception> Function<T, R> wrapper(FunctionWithException<T, R, E> function){
        return arg-> {
            try{
                return function.apply(arg);
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        };

    }
}
