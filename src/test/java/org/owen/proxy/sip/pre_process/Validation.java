package org.owen.proxy.sip.pre_process;

/**
 * Created by dongqlee on 2018. 5. 16..
 */
public class Validation {
    private boolean isValidate;
    private int responseCode;
    private String reason;

    public Validation() {
        this.isValidate=true;
        this.responseCode=0;
        this.reason="";
    }

    public boolean isValidate() {
        return isValidate;
    }

    public void setValidate(boolean validate) {
        isValidate = validate;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
