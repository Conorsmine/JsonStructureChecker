package com.conorsmine.net.json_schema.errors;

public class JsonFormatCheckError extends Error {
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
