package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.TagType;
import com.conorsmine.net.json_schema.errors.JsonTypeMissmatchError;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TagBoolean extends JsonTag {

    public static final Set<String> DEFAULT_TRUE = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("true", "1", "yes", "y", "on", "enable", "t")));
    public static final Set<String> DEFAULT_FALSE = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("false", "0", "no", "n", "off", "disable", "f")));

    final Set<String> validBools;
    final Set<String> invalidBools;

    private TagBoolean(boolean optional, Set<String> validBools, Set<String> invalidBools) {
        super(TagType.BOOL, optional);
        this.validBools = Collections.unmodifiableSet(validBools);
        this.invalidBools = Collections.unmodifiableSet(invalidBools);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean set = true;
        private boolean optional = false;
        private final Set<String> validBools = new HashSet<>(DEFAULT_TRUE);
        private final Set<String> invalidBools = new HashSet<>(DEFAULT_FALSE);

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
         * Adds a boolean to the set of valid booleans. <br>
         * Capitalization will be ignored!
         *
         * @param bool Boolean to add
         */
        public Builder addValidBool(final String bool) {
            if (set) { validBools.clear(); set = false; }

            this.validBools.add(bool);
            return this;
        }

        /**
         * Adds a boolean to the set of invalid booleans. <br>
         * Capitalization will be ignored!
         *
         * @param bool Boolean to add
         */
        public Builder addInvalidBool(final String bool) {
            if (set) { validBools.clear(); set = false; }

            this.invalidBools.add(bool);
            return this;
        }

        /**
         * Adds an array of booleans to the set of valid booleans. <br>
         * Capitalization will be ignored!
         *
         * @param bools Array of booleans to add
         */
        public Builder addValidBools(final String... bools) {
            if (set) { validBools.clear(); set = false; }
            this.validBools.addAll(Arrays.asList(bools));
            return this;
        }

        /**
         * Adds an array of booleans to the set of invalid booleans. <br>
         * Capitalization will be ignored!
         *
         * @param bools Array of booleans to add
         */
        public Builder addInvalidBools(final String... bools) {
            if (set) { validBools.clear(); set = false; }
            this.invalidBools.addAll(Arrays.asList(bools));
            return this;
        }

        /**
         * Adds a collection of booleans to the set of valid booleans. <br>
         * Capitalization will be ignored!
         *
         * @param bools Collection of booleans to add
         */
        public Builder addValidBools(final Collection<String> bools) {
            if (set) { validBools.clear(); set = false; }
            this.validBools.addAll(bools);
            return this;
        }

        /**
         * Adds a collection of booleans to the set of invalid booleans. <br>
         * Capitalization will be ignored!
         *
         * @param bools Collection of booleans to add
         */
        public Builder addInvalidBools(final Collection<String> bools) {
            if (set) { validBools.clear(); set = false; }
            this.invalidBools.addAll(bools);
            return this;
        }

        // Bools added after this point are not added to the set of (in)valid booleans
        public TagBoolean build() {
            return new TagBoolean(optional, validBools, invalidBools);
        }

    }

    @Override
    public boolean isValid(@NotNull JsonElement tag, @NotNull CheckResult.Builder errorBuilder, @Nullable String path) {
        path = (path == null) ? "" : path;

        if (!tag.isJsonPrimitive()) {
            errorBuilder.addError(path, new JsonTypeMissmatchError(path, TagType.BOOL, tag));
            return false;
        }

        String bool = tag.getAsString();
        if (!(validBools.contains(bool) || invalidBools.contains(bool))) {
            errorBuilder.addError(path, new JsonTypeMissmatchError(path, type, tag));
            return false;
        }

        return true;
    }

}
