package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.TagType;
import com.conorsmine.net.json_schema.errors.JsonFormatCheckError;
import com.conorsmine.net.json_schema.errors.JsonMissingTagError;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagConditional {

    private final String referenceValue, destinationKey;
    private final boolean required;
    private final JsonTag tagFormat;

    public TagConditional(String referenceValue, String destinationKey, boolean required, JsonTag tagFormat) {
        this.referenceValue = referenceValue;
        this.destinationKey = destinationKey;
        this.required = required;
        this.tagFormat = tagFormat;
    }

    public static TagConditional create(final @NotNull String referenceValue, final @NotNull String destinationKey, boolean required, final @NotNull JsonTag tagFormat) {
        return new TagConditional(referenceValue, destinationKey, required, tagFormat);
    }

    public static TagConditional.Builder builder() {
        return new TagConditional.Builder();
    }

    // Just here for consistency
    public static class Builder {

        boolean required = true;

        private Builder() { }

        public Builder setNotRequired() {
            this.required = false;
            return this;
        }

        public TagConditional build(final @NotNull String referenceValue, final @NotNull String destinationKey, final @NotNull JsonTag tagFormat) {
            return create(referenceValue, destinationKey, required, tagFormat);
        }
    }


    static class SuperSecretTagConditionalCollection extends JsonTag {

        private final String referenceKey;
        private final TagConditional[] conditionals;

        SuperSecretTagConditionalCollection(String referenceKey, TagConditional[] conditionals) {
            super(TagType.CONDITIONAL, false);
            this.referenceKey = referenceKey;
            this.conditionals = conditionals;
        }

        @Override
        public boolean isValid(@NotNull JsonElement tag, @NotNull CheckResult.Builder errorBuilder, @Nullable String path) {
            if (!tag.isJsonObject()) throw new IllegalStateException("The json tag is not a JsonObject! This should not happen! (Please report this as a bug)");
            final JsonObject json = tag.getAsJsonObject();

            for (TagConditional conditional : conditionals) {
                if (!json.get(referenceKey).getAsString().equalsIgnoreCase(conditional.referenceValue)) continue;

                return handleConditional(json, errorBuilder, path, conditional);
            }

            return true;
        }

        private boolean handleConditional(JsonObject value, CheckResult.Builder builder, String path, TagConditional conditional) {
            final String destinationKey = conditional.destinationKey;
            final JsonTag tagFormat = conditional.tagFormat;

            final Boolean result = doChecks(value, builder, path, conditional);
            if (result != null) return result;

            final JsonElement tag = value.get(destinationKey);
            if (tag == null) {
                final String destinationPath = (path == null) ? destinationKey : path + "." + destinationKey;
                if (tagFormat.isOptional()) return true;
                builder.addError(destinationPath, new JsonMissingTagError(destinationPath, tagFormat.getType()));
                return false;
            }

            return conditional.tagFormat.isValid(tag, builder, path);
        }

        // Returns null if the code should continue, otherwise a return value
        private Boolean doChecks(JsonObject value, CheckResult.Builder builder, String path, TagConditional conditional) {
            if (!value.has(referenceKey)) {
                if (conditional.tagFormat.isOptional()) return true;    // This is a bit weird
                builder.addError(path, new JsonFormatCheckError("Missing reference key \"" + referenceKey + "\" for conditionals!"));
                return false;
            }

            if (!value.get(referenceKey).isJsonPrimitive()) {
                builder.addError(path, new JsonFormatCheckError("Reference key \"" + referenceKey + "\" is not a primitive type!"));
                return false;
            }

            if (!value.has(conditional.destinationKey)) {
                if (!conditional.required) return true;
                builder.addError(path, new JsonFormatCheckError("Missing destination key \"" + conditional.destinationKey + "\" for conditionals!"));
                return false;
            }

            return null;
        }
    }

}
