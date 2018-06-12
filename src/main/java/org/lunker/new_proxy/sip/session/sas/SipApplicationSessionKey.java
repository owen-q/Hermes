package org.lunker.new_proxy.sip.session.sas;

import gov.nist.javax.sip.message.SIPMessage;
import org.lunker.new_proxy.util.HashUtil;

import java.util.UUID;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class SipApplicationSessionKey {

    private String uuid;
    private String generatedKey="";
    private int MAX_HASHED_LENGTH=8;

    public SipApplicationSessionKey() {
        this.uuid=""+UUID.randomUUID();
        generateKey();
    }

    public SipApplicationSessionKey(SIPMessage sipMessage) {
        generateKey();
    }

    public SipApplicationSessionKey(String generatedKey) {
        this.generatedKey = generatedKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            SipApplicationSessionKey other=(SipApplicationSessionKey)obj;

            if(!other.getGeneratedKey().equals(this.getGeneratedKey())){
                return false;
            }
            else
                return true;
        }
    }

    @Override
    public int hashCode() {
        int result = 1;
//        result = 31 * result + (this.applicationName == null ? 0 : this.applicationName.hashCode());
//        result = 31 * result + (this.uuid == null ? 0 : this.uuid.hashCode());
        result = 31 * result + (this.generatedKey.equals("") ? 0 : this.generatedKey.hashCode());
        return result;
    }

    private void generateKey(){
        this.generatedKey=HashUtil.hashString(this.uuid, MAX_HASHED_LENGTH);// hash
    }

    public String getGeneratedKey() {
        return this.generatedKey;
    }

    @Override
    public String toString() {
        return this.generatedKey;
    }
}
