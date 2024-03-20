package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.TagType;
import com.conorsmine.net.json_schema.errors.JsonMissingTagError;
import com.conorsmine.net.json_schema.errors.JsonTypeMissmatchError;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TagObj extends JsonTag {

    private final Map<String, JsonTag> keyTagMap;

    private TagObj(Map<String, JsonTag> keyTagMap, boolean optional) {
        super(TagType.OBJ, optional);
        this.keyTagMap = Collections.unmodifiableMap(keyTagMap);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements TagBuilder<TagObj> {

        private boolean optional = false;
        final Map<String, JsonTag> keyTagMap = new HashMap<>();

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
         * Sets a key to be of the provided tag.
         * @param key Key to be set
         * @param tag Tag to be used
         */
        public Builder setKeyAs(final @NotNull String key, final @NotNull JsonTag tag) {
            keyTagMap.put(key, tag);
            return this;
        }

        /**
         * Sets a key to be of the provided type. This method is a shortcut for the other setKeyAs methods. <br>
         * <b>NOTE:</b> This method is not implemented for all types.
         * @param key Key to be set
         * @param type Type to be used
         */
        public Builder setKeyAs(final @NotNull String key, final @NotNull TagType type) {
            keyTagMap.put(key, type.getTag());
            return this;
        }

        public Builder setConditionalTags(final @NotNull String referenceKey, final @NotNull TagConditional... conditionals) {
            keyTagMap.put(referenceKey, new TagConditional.SuperSecretTagConditionalCollection(referenceKey, conditionals));
            return this;
        }

        public TagObj build() {
            return new TagObj(keyTagMap, optional);
        }

    }

    @Override
    public boolean isValid(@NotNull JsonElement tag, @NotNull CheckResult.Builder errorBuilder, @Nullable String path) {
        final boolean isFirst = (path == null);
        path = (isFirst) ? "" : path;

        if (!tag.isJsonObject()) {
            errorBuilder.addError(path, new JsonTypeMissmatchError(path, type, tag));
            return false;
        }

        boolean valid = true;
        for (Map.Entry<String, JsonTag> entry : keyTagMap.entrySet()) {
            final String key = entry.getKey();
            final String keyPath = String.format("%s%s%s", path, (isFirst) ? "" : ".", key);
            JsonTag tagObj = entry.getValue();
            final JsonElement tagElement = tag.getAsJsonObject().get(key);

            if (tagElement == null && !tagObj.isOptional()) {
                errorBuilder.addError(keyPath, new JsonMissingTagError(keyPath, tagObj.getType()));
                valid = false;
                continue;
            }
            else if (tagElement == null) continue;

            // Handle groups
            // Sets the tagObj to the group tag if the tagObj is a group
            if (tagObj.getType() == TagType.GROUP) {
                final String groupName = ((TagGroup) tagObj).getGroupName();
                tagObj = errorBuilder.getGroupMap().get(groupName);
                if (tagObj == null) throw new IllegalStateException("Group not found for key: " + groupName);
            }

            // Handle conditionals
            if (tagObj.getType() == TagType.CONDITIONAL) {
                final TagConditional.SuperSecretTagConditionalCollection conditionalCollection = (TagConditional.SuperSecretTagConditionalCollection) tagObj;
                if (conditionalCollection.isValid(((JsonObject) tag), errorBuilder, keyPath)) continue;
                valid = false;
            }

            // Handle other tags
            else if (!tagObj.isValid(tagElement, errorBuilder, keyPath)) valid = false;
        }

        return valid;
    }
}
