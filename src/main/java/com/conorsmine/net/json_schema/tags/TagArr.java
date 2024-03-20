package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.TagType;
import com.conorsmine.net.json_schema.errors.JsonIncorrectSizeError;
import com.conorsmine.net.json_schema.errors.JsonMissingTagError;
import com.conorsmine.net.json_schema.errors.JsonTypeMissmatchError;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagArr extends JsonTag {

    final JsonTag tagFromat;
    final Long minSize, maxSize;

    private TagArr(boolean optional, JsonTag tagFromat, Long minSize, Long maxSize) {
        super(TagType.ARR, optional);
        this.tagFromat = tagFromat;
        this.minSize = minSize;
        this.maxSize = maxSize;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements TagBuilder<TagArr> {
        private JsonTag tagFromat = TagType.ANY.getTag();
        private Long minSize, maxSize;
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

        /**
         * Sets the tag format to be used for the array.
         * @param type Type to be used
         */
        public Builder setTagFormat(final @NotNull TagType type) {
            this.tagFromat = type.getTag();
            return this;
        }

        /**
         * Sets the tag format to be used for the array.
         * @param tagFromat Tag to be used
         */
        public Builder setTagFormat(final @NotNull JsonTag tagFromat) {
            this.tagFromat = tagFromat;
            return this;
        }

        /**
         * Sets the minimum size of the array.
         * @param minSize Minimum size to be set
         */
        public Builder setMinSize(final @NotNull Long minSize) {
            if (minSize < 0) throw new UnsupportedOperationException("The minSize value must be >= 0!");
            this.minSize = minSize;
            return this;
        }

        /**
         * Sets the maximum size of the array.
         * @param maxSize Maximum size to be set
         */
        public Builder setMaxSize(final @NotNull Long maxSize) {
            if (maxSize < 0) throw new UnsupportedOperationException("The maxSize value must be >= 0!");
            this.maxSize = maxSize;
            return this;
        }

        /**
         * Sets the size of the array.
         * @param size Size to be set
         */
        public Builder setSize(final @NotNull Long size) {
            setMinSize(size);
            setMaxSize(size);
            return this;
        }

        public TagArr build() {
            return new TagArr(optional, tagFromat, minSize, maxSize);
        }
    }

    @Override
    public boolean isValid(@NotNull JsonElement tag, @NotNull CheckResult.Builder errorBuilder, @Nullable String path) {
        path = (path == null) ? "" : path;

        if (!tag.isJsonArray()) {
            errorBuilder.addError(path, new JsonTypeMissmatchError(path, type, tag));
            return false;
        }

        final int size = tag.getAsJsonArray().size();
        if (minSize != null && size < minSize) {
            errorBuilder.addError(path, JsonIncorrectSizeError.getTooFew(path, minSize));
            return false;
        }
        if (maxSize != null && size > maxSize) {
            errorBuilder.addError(path, JsonIncorrectSizeError.getTooMany(path, maxSize));
            return false;
        }

        boolean valid = true;
        int counter = 0;
        for (JsonElement jsonElement : tag.getAsJsonArray()) {
            final String newPath = path + "[" + counter++ + "]";
            JsonTag arrTagFromat = tagFromat;

            if (jsonElement == null) {
                errorBuilder.addError(newPath, new JsonMissingTagError(newPath, arrTagFromat.getType()));
                valid = false;
                continue;
            }

            if (arrTagFromat.getType() == TagType.GROUP) {
                final String groupName = ((TagGroup) tagFromat).getGroupName();
                arrTagFromat = errorBuilder.getGroupMap().get(groupName);
                if (arrTagFromat == null) throw new IllegalStateException("Group not found for key: " + groupName);
            }


            if (!arrTagFromat.isValid(jsonElement, errorBuilder, newPath)) valid = false;
        }

        return valid;
    }
}
