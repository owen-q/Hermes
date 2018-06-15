package org.owen.hermes.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by owen.q on 2018. 6. 14..
 */
public class CheckUtil {
    private Logger logger = LoggerFactory.getLogger(CheckUtil.class);

    private CheckUtil() {
    }

    public static void checkNotNull(Object arg, String name){
        if(arg == null)
            throw new IllegalArgumentException(name + " expected: !=null, actual: null");
    }

    public static void checkNotZero(int arg, String name){
        if(arg == 0)
            throw new IllegalArgumentException(name + " expected: !=0, actual: " + arg);
    }

    public static void checkEmptyString(String arg, String name){
        if(arg.equals(""))
            throw new IllegalArgumentException(name + " expected: != \"\", actual: empty string");
    }

    public static <T extends Object>void checkNotEqual(T expected, T actual, String name){
        if(expected.equals(actual)){
            throw new IllegalArgumentException(name + " expected: != " + expected.toString() + ", actual: " + actual.toString());
        }
    }

    public static void checkCollectionNotEmpty(Collection arg, String name){
        if(arg.size() == 0)
            throw new IllegalArgumentException(name + " expected: size > 0, actual: empty collection");
    }

    private static void fail(String message){
        //
    }
}
