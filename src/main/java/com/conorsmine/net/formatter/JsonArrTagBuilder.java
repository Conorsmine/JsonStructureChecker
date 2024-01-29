package com.conorsmine.net.formatter;

public class JsonArrTagBuilder extends JsonTagBuilder {

    final JsonFormatBuilder.JsonType arrType;
    Integer minSize, maxSize;

    public JsonArrTagBuilder(JsonObjFormatBuilder builderRef, JsonFormatBuilder.JsonType arrType) {
        super(builderRef, JsonFormatBuilder.JsonType.ARR);
        this.arrType = arrType;
    }

    /**
     * Sets a minimum allowed size for the array.
     *
     * @param minSize Smallest number of elements allowed
     */
    public JsonArrTagBuilder setMinSize(int minSize) {
        if (minSize < 0) throw new UnsupportedOperationException("The minSize value must be >= 0!");

        this.minSize = minSize;
        return this;
    }

    /**
     * Sets a maximum allowed size for the array.
     *
     * @param maxSize Largest number of elements allowed
     */
    public JsonArrTagBuilder setMaxSize(int maxSize) {
        if (maxSize < 0) throw new UnsupportedOperationException("The maxSize value must be >= 0!");

        this.maxSize = maxSize;
        return this;
    }

    /**
     * Sets a specific size for the array.
     */
    public JsonArrTagBuilder setSize(int size) {
        setMinSize(size).setMaxSize(size);
        return this;
    }
}
