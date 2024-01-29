package com.conorsmine.net.formatter.errors;

public class JsonFormatCheckError extends Error{
    public JsonFormatCheckError() { }

    public JsonFormatCheckError(String message) {
        super(message);
    }

    public JsonFormatCheckError(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonFormatCheckError(Throwable cause) {
        super(cause);
    }
}
