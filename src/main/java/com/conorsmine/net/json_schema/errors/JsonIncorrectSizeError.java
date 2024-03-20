package com.conorsmine.net.json_schema.errors;

public class JsonIncorrectSizeError extends JsonFormatCheckError {

    private JsonIncorrectSizeError(String message) {
        super(message);
    }

    public static JsonIncorrectSizeError getTooFew(String path, Number shouldSize) {
        return new JsonIncorrectSizeError(String.format("The array \"%s\" has too few elements. It should have at least %s.", path, shouldSize));
    }

    public static JsonIncorrectSizeError getTooMany(String path, Number shouldSize) {
        return new JsonIncorrectSizeError(String.format("The array \"%s\" has too many elements. It should have at most %s.", path, shouldSize));
    }

    public static JsonIncorrectSizeError getOutsideRange(String path, Number minSize, Number maxSize) {
        return new JsonIncorrectSizeError(String.format("The size of the array \"%s\" is outside of the valid range. It should have at least %s and at max %s elements.", path, minSize, maxSize));
    }

    public static JsonIncorrectSizeError getIncorrectSize(String path, Number shouldSize) {
        return new JsonIncorrectSizeError(String.format("The array \"%s\" does not have the correct amount of elements. It should have %s elements.", path, shouldSize));
    }
}
