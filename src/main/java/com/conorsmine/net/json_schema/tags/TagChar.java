package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.TagType;
import com.conorsmine.net.json_schema.errors.JsonIncorrectValueError;
import com.conorsmine.net.json_schema.errors.JsonTypeMissmatchError;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TagChar extends JsonTag {

    private final Set<Character> validChars;

    private TagChar(boolean optional, Set<Character> validChars) {
        super(TagType.CHAR, optional);
        this.validChars = Collections.unmodifiableSet(validChars);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean optional = false;
        private final Set<Character> validChars = new HashSet<>();

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
         * Adds a character to the set of valid characters.
         *
         * @param c Character to add
         */
        public Builder addValidChar(final char c) {
            this.validChars.add(c);
            return this;
        }

        /**
         * Adds an array of characters to the set of valid characters.
         *
         * @param c Array of characters to add
         */
        public Builder addValidChars(final char... c) {
            for (char ch : c) this.validChars.add(ch);
            return this;
        }

        /**
         * Adds a string of characters to the set of valid characters.
         *
         * @param s String of characters to add
         */
        public Builder addValidChars(final String s) {
            for (char c : s.toCharArray()) this.validChars.add(c);
            return this;
        }

        /**
         * Adds a collection of characters to the set of valid characters.
         *
         * @param c Collection of characters to add
         */
        public Builder addValidChars(final Collection<Character> c) {
            this.validChars.addAll(c);
            return this;
        }

        public TagChar build() {
            return new TagChar(optional, validChars);
        }

    }

    @Override
    public boolean isValid(@NotNull JsonElement tag, @NotNull CheckResult.Builder errorBuilder, @Nullable String path) {
        path = path == null ? "" : path;

        if (!tag.isJsonPrimitive() || tag.getAsString().length() != 1) {
            errorBuilder.addError(path, new JsonTypeMissmatchError(path, type, tag));
            return false;
        }
        if (validChars.isEmpty()) return true;
        if (!validChars.contains(tag.getAsString().charAt(0))) {
            errorBuilder.addError(path, new JsonIncorrectValueError(path, tag, type, validChars));
            return false;
        }

        return true;
    }
}
