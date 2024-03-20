package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.TagType;
import com.conorsmine.net.json_schema.errors.JsonFormatCheckError;
import com.conorsmine.net.json_schema.errors.JsonTypeMissmatchError;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class TagEnum extends JsonTag {

    final String[] enumConstants;

    private TagEnum(boolean optional, String[] enumConstants) {
        super(TagType.ENUM, optional);
        this.enumConstants = enumConstants;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static TagEnum create(final boolean optional, final @NotNull Class<? extends Enum<?>> enumClass) {
        final Enum<?>[] enums = enumClass.getEnumConstants();
        if (enums == null) throw new IllegalArgumentException("The class " + enumClass.getName() + " is not an enum!");

        return new TagEnum(optional, Arrays.stream(enums).map(Enum::name).toArray(String[]::new));
    }

    public static TagEnum create(final @NotNull Class<? extends Enum<?>> enumClass) {
        return create(false, enumClass);
    }

    public static TagEnum create(final boolean optional, final @NotNull String... enumConstants) {
        return new TagEnum(optional, enumConstants);
    }

    public static TagEnum create(final @NotNull String... enumConstants) {
        return create(false, enumConstants);
    }

    // Just here for consistency
    public static class Builder {

        private boolean optional = false;
        private String[] enumConstants = new String[0];

        private Builder() { }

        public Builder setOptional(final boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder setOptional() {
            this.optional = true;
            return this;
        }

        public Builder setEnumClass(final @NotNull Class<? extends Enum<?>> enumClass) {
            final Enum<?>[] enums = enumClass.getEnumConstants();
            if (enums == null) throw new IllegalArgumentException("The class " + enumClass.getName() + " is not an enum!");

            this.enumConstants = Arrays.stream(enums).map(Enum::name).toArray(String[]::new);
            return this;
        }

        public Builder setEnumConstants(final @NotNull String... enumConstants) {
            this.enumConstants = enumConstants;
            return this;
        }

        public TagEnum build() {
            return new TagEnum(optional, enumConstants);
        }
    }

    @Override
    public boolean isValid(@NotNull JsonElement tag, @NotNull CheckResult.Builder errorBuilder, @Nullable String path) {
        path = path == null ? "" : path;

        if (!tag.isJsonPrimitive()) {
            errorBuilder.addError(path, new JsonTypeMissmatchError(path, type, tag));
            return false;
        }

        for (String constant : enumConstants)
            if (constant.equalsIgnoreCase(tag.getAsString())) return true;

        errorBuilder.addError(path, new JsonFormatCheckError(String.format("\"%s\" is not a valid enum constant!", tag.getAsString())));
        return false;
    }
}
