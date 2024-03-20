package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.TagType;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagGroup extends JsonTag {

    private final String groupName;

    TagGroup(final String groupName, boolean optional) {
        super(TagType.GROUP, optional);
        this.groupName = groupName.toLowerCase();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static TagGroup create(final @NotNull String groupName, final boolean optional) {
        return new TagGroup(groupName, optional);
    }

    public static TagGroup create(final @NotNull String groupName) {
        return create(groupName, false);
    }

    // Just here for consistency
    public static class Builder {

        private boolean optional = false;

        private Builder() { }

        public Builder setOptional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder setOptional() {
            this.optional = true;
            return this;
        }

        public TagGroup build(final @NotNull String groupName) {
            return new TagGroup(groupName, optional);
        }
    }

    @NotNull
    public String getGroupName() {
        return groupName;
    }

    @Override
    @Deprecated
    public boolean isValid(@NotNull JsonElement tag, @NotNull CheckResult.Builder errorBuilder, @Nullable String path) {
        throw new UnsupportedOperationException("#isValid should never be called for a TagGroup! Report this to the author!");
    }
}
