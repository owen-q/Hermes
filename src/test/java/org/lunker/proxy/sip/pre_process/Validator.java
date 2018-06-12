package org.lunker.proxy.sip.pre_process;

import org.lunker.proxy.core.Message;

import java.util.function.Predicate;

/**
 * Created by dongqlee on 2018. 5. 23..
 */
public class Validator implements Predicate<Message> {
    @Override
    public boolean test(Message message) {
        return message.getValidation().isValidate();
    }
}