package com.conorsmine.net.formatter.errors;

public class JsonIncorrectSizeError extends JsonFormatCheckError {

    private JsonIncorrectSizeError(String message) {
        super(message);
    }

    public static JsonIncorrectSizeError getTooFew(String path, int shouldSize) {
        return new JsonIncorrectSizeError(String.format("The array \"%s\" has too few elements. It should have at least %d.", path, shouldSize));
    }

    public static JsonIncorrectSizeError getTooMany(String path, int shouldSize) {
        return new JsonIncorrectSizeError(String.format("The array \"%s\" has too many elements. It should have at most %d.", path, shouldSize));
    }

    public static JsonIncorrectSizeError getIncorrectSize(String path, int shouldSize) {
        return new JsonIncorrectSizeError(String.format("The array \"%s\" does not have the correct amount of elements. It should have %d elements.", path, shouldSize));
    }
}
