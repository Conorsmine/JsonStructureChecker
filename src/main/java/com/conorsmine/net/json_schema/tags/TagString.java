package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.TagType;
import com.conorsmine.net.json_schema.errors.JsonIncorrectSizeError;
import com.conorsmine.net.json_schema.errors.JsonTypeMissmatchError;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagString extends JsonTag {

    final Long minLen, maxLen;

    private TagString(Long minLen, Long maxLen, boolean optional) {
        super(TagType.STR, optional);
        this.minLen = minLen;
        this.maxLen = maxLen;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean optional = false;
        Long minLen, maxLen;

        private Builder() { }

        public Builder setOptional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder setOptional() {
            this.optional = true;
            return this;
        }

        /**
         * Sets the minimum length of the string.
         *
         * @param minLen Minimum length of the string
         */
        public Builder setMinLen(long minLen) {
            if (minLen < 0) throw new UnsupportedOperationException("The minLen value must be >= 0!");
            this.minLen = minLen;
            return this;
        }

        /**
         * Sets the maximum length of the string.
         *
         * @param maxLen Maximum length of the string
         */
        public Builder setMaxLen(long maxLen) {
            if (maxLen < 0) throw new UnsupportedOperationException("The maxLen value must be >= 0!");
            this.maxLen = maxLen;
            return this;
        }

        /**
         * Sets the minimum and maximum length of the string.
         *
         * @param minLen Minimum length of the string
         * @param maxLen Maximum length of the string
         */
        public Builder setLenRange(long minLen, long maxLen) {
            this.minLen = minLen;
            this.maxLen = maxLen;
            return this;
        }

        /**
         * Sets the length of the string.
         *
         * @param len Length of the string
         */
        public Builder setLen(long len) {
            setMinLen(len);
            setMaxLen(len);
            return this;
        }

        public TagString build() {
            return new TagString(minLen, maxLen, optional);
        }
    }

    @Override
    public boolean isValid(@NotNull JsonElement tag, @NotNull CheckResult.Builder errorBuilder, @Nullable String path) {
        path = path == null ? "" : path;

        if (!tag.isJsonPrimitive()) {
            errorBuilder.addError(path, new JsonTypeMissmatchError(path, type, tag));
            return false;
        }
        if (minLen == null && maxLen == null) return true;

        if (minLen == null && !(tag.getAsString().length() <= maxLen)) {
            errorBuilder.addError(path, JsonIncorrectSizeError.getTooMany(path, minLen));
            return false;
        }
        if (maxLen == null && !(tag.getAsString().length() >= minLen)) {
            errorBuilder.addError(path, JsonIncorrectSizeError.getTooFew(path, maxLen));
            return false;
        }
        if (!(tag.getAsString().length() >= minLen && tag.getAsString().length() <= maxLen)) {
            errorBuilder.addError(path, JsonIncorrectSizeError.getIncorrectSize(path, minLen));
            return false;
        }

        return true;
    }
}
