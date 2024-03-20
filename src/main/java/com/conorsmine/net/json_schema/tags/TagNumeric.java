package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.TagType;
import com.conorsmine.net.json_schema.errors.JsonIncorrectSizeError;
import com.conorsmine.net.json_schema.errors.JsonTypeMissmatchError;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagNumeric<T extends Number> extends JsonTag {

    private final T minValue, maxValue;

    private TagNumeric(TagType type, T minValue, T maxValue, boolean optional) {
        super(type, optional);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public static <T extends Number> Builder<T> builder(final @NotNull TagType type) {
        return new Builder<>(type);
    }

    public static class Builder<T extends Number> {

        private boolean optional = false;
        final TagType type;
        T minValue, maxValue;

        private Builder(TagType type) { this.type = type; }

        public Builder<T> setOptional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder<T> setOptional() {
            this.optional = true;
            return this;
        }

        /**
         * Sets the minimum value for the number.
         * @param minValue The smallest number allowed
         */
        public Builder<T> setMinValue(T minValue) {
            this.minValue = minValue;
            return this;
        }

        /**
         * Sets the maximum value for the number.
         * @param maxValue The largest number allowed
         */
        public Builder<T> setMaxValue(T maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        /**
         * Sets the minimum and maximum values for the number.
         * @param minValue The smallest number allowed
         * @param maxValue The largest number allowed
         */
        public Builder<T> setRange(T minValue, T maxValue) {
            setMinValue(minValue).setMaxValue(maxValue);
            return this;
        }

        public TagNumeric<T> build() {
            return new TagNumeric<>(type, minValue, maxValue, optional);
        }
    }

    @Override
    public boolean isValid(@NotNull JsonElement tag, @NotNull CheckResult.Builder errorBuilder, @Nullable String path) {
        path = path == null ? "" : path;

        if (!isNumberOfType(tag, type)) {
            errorBuilder.addError(path, new JsonTypeMissmatchError(path, type, tag));
            return false;
        }

        if (minValue == null && maxValue == null) return true;
        if (minValue == null && !(tag.getAsNumber().doubleValue() <= maxValue.doubleValue())) {
            errorBuilder.addError(path, JsonIncorrectSizeError.getTooMany(path, minValue));
            return false;
        }
        if (maxValue == null && !(tag.getAsNumber().doubleValue() >= minValue.doubleValue())) {
            errorBuilder.addError(path, JsonIncorrectSizeError.getTooFew(path, maxValue));
            return false;
        }

        if (!(tag.getAsNumber().doubleValue() >= minValue.doubleValue() && tag.getAsNumber().doubleValue() <= maxValue.doubleValue())) {
            errorBuilder.addError(path, JsonIncorrectSizeError.getOutsideRange(path, minValue, maxValue));
            return false;
        }

        return true;
    }

    private static boolean isNumberOfType(final JsonElement tag, final TagType type) {
        if (!tag.isJsonPrimitive() || !tag.getAsJsonPrimitive().isNumber()) return false;
        final Number num = tag.getAsNumber();
        switch (type) {
            case U_INT: return num.longValue() >= 0;
            case INT: return num instanceof Long;
            case S_INT: return num.longValue() <= 0;
            case U_FLT: return num.doubleValue() >= 0;
            case FLT: return num instanceof Double;
            case S_FLT: return num.doubleValue() <= 0;
            default: throw new UnsupportedOperationException("This type is not supported! " + type);    // This should never happen
        }
    }
}
