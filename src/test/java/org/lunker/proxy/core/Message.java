package org.lunker.proxy.core;

import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipResponse;
import org.lunker.proxy.sip.pre_process.Validation;

/**
 * Created by dongqlee on 2018. 5. 17..
 */
public class Message {
    private ProcessState processState;
    private DefaultSipMessage originalMessage;
    private DefaultSipMessage newMessage;
    private Validation validation;

    public Message() {
    }

    public Message(DefaultSipMessage originalMessage) {
        this.processState = ProcessState.PRE;
        this.originalMessage = originalMessage;
        this.newMessage = originalMessage;
        this.validation = new Validation();
    }

    public Message(DefaultSipMessage originalMessage, Validation validation) {
        this.processState= ProcessState.PRE;
        this.originalMessage = originalMessage;
        this.newMessage = null;
//        this.validation = validation;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public DefaultSipMessage getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(DefaultSipMessage originalMessage) {
        this.originalMessage = originalMessage;
    }

    public DefaultSipMessage getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(DefaultSipMessage newMessage) {
        this.newMessage = newMessage;
    }

    public Validation getValidation() {
        return validation;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public void validate(int responseCode, String reason, DefaultSipResponse newMessage){

    }

    public void invalidate(int responseCode, String reason, DefaultSipResponse errorResponse){
        this.validation.setValidate(false);
        this.validation.setResponseCode(responseCode);
        this.validation.setReason(reason);
        this.setNewMessage(errorResponse);
    }
}
