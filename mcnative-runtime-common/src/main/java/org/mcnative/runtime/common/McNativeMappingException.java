package org.mcnative.runtime.common;

public class McNativeMappingException extends RuntimeException {

    public McNativeMappingException(String message) {
        super(message);
    }

    public McNativeMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public McNativeMappingException(Throwable cause) {
        super(cause);
    }

    public McNativeMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
