package org.lunker.proxy.util.lambda;

import org.lunker.new_proxy.sip.wrapper.message.DefaultSipRequest;
import org.lunker.new_proxy.util.lambda.FunctionWithException;

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
                if(arg instanceof DefaultSipRequest){
                    // TODO: Create 500 Server Internal Error
                }


                throw new RuntimeException(e);
            }
        };

    }
}
