package org.lunker.new_proxy.exception;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class MaxMessageSizeExceededException extends Exception{
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     */
    public MaxMessageSizeExceededException(final String message) {
        super(message);
    }
}
