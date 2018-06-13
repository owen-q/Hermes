package org.owen.proxy.core;

/**
 * Created by dongqlee on 2018. 5. 17..
 */
public enum ProcessState {
    PRE("pre"),
    IN("in"),
    POST("post"),
    COMPLETE("complete");

    private String value;

    ProcessState(String value) {
        this.value = value;
    }
}
