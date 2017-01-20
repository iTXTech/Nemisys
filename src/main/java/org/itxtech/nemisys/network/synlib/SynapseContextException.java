package org.itxtech.nemisys.network.synlib;

/**
 * SynapseContextException
 * ===============
 * author: boybook
 * Nemisys Project
 * ===============
 */
public class SynapseContextException extends Exception {

    public SynapseContextException(){
        super();
    }

    public SynapseContextException(String message) {
        super(message);
    }

    public SynapseContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public SynapseContextException(Throwable cause) {
        super(cause);
    }
    
}
