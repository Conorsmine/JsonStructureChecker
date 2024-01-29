package com.conorsmine.net.formatter;

import com.conorsmine.net.formatter.errors.JsonMissingTagError;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.conorsmine.net.formatter.errors.JsonFormatCheckError;
import com.conorsmine.net.formatter.errors.JsonIncorrectSizeError;
import com.conorsmine.net.formatter.errors.JsonTypeMissmatchError;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class JsonObjFormatBuilder implements IFormatBuilder {

    final Map<String, JsonTagBuilder> keyTagMap = new HashMap<>();

    /**
     * Sets the value at the key to be an unsigned integer value.
     *
     * @param key Key in the Json
     */
    public JsonTagBuilder setKeyAsUInt(final @NotNull String key) {
        final JsonTagBuilder builder = new JsonTagBuilder(this, JsonFormatBuilder.JsonType.U_INT);
        this.keyTagMap.put(key, builder);
        return builder;
    }

    /**
     * Sets the value at the key to be an integer value.
     *
     * @param key Key in the Json
     */
    public JsonTagBuilder setKeyAsInt(final @NotNull String key) {
        final JsonTagBuilder builder = new JsonTagBuilder(this, JsonFormatBuilder.JsonType.INT);
        this.keyTagMap.put(key, builder);
        return builder;
    }

    /**
     * Sets the value at the key to be an unsigned floating point value.
     *
     * @param key Key in the Json
     */
    public JsonTagBuilder setKeyAsUFlt(final @NotNull String key) {
        final JsonTagBuilder builder = new JsonTagBuilder(this, JsonFormatBuilder.JsonType.U_FLT);
        this.keyTagMap.put(key, builder);
        return builder;
    }

    /**
     * Sets the value at the key to be a floating point value.
     *
     * @param key Key in the Json
     */
    public JsonTagBuilder setKeyAsFlt(final @NotNull String key) {
        final JsonTagBuilder builder = new JsonTagBuilder(this, JsonFormatBuilder.JsonType.FLT);
        this.keyTagMap.put(key, builder);
        return builder;
    }

    /**
     * Sets the value at the key to be a string. <br>
     * This has the effect that anything can be written there, even an empty/blank string!
     *
     * @param key Key in the Json
     */
    public JsonTagBuilder setKeyAsStr(final @NotNull String key) {
        final JsonTagBuilder builder = new JsonTagBuilder(this, JsonFormatBuilder.JsonType.STR);
        this.keyTagMap.put(key, builder);
        return builder;
    }

    /**
     * Sets the value at the key to be another JsonObject.
     *
     * @param key Key in the Json
     * @param format The format of the JsonObject. See also {@link JsonFormatBuilder#createObjBuilder()}
     */
    public JsonObjTagBuilder setKeyAsObj(final @NotNull String key, final @NotNull JsonObjFormatBuilder format) {
        final JsonObjTagBuilder builder = new JsonObjTagBuilder(this, format);
        this.keyTagMap.put(key, builder);
        return builder;
    }

    /**
     * Sets the value at the key to be an array of a specific type.
     *
     * @param key Key in the Json
     * @param arrType Type of the array. {@link JsonFormatBuilder.JsonType#OBJ} and {@link JsonFormatBuilder.JsonType#ARR} are not supported!
     */
    public JsonArrTagBuilder setKeyAsArr(final @NotNull String key, final @NotNull JsonFormatBuilder.JsonType arrType) {
        if (arrType == JsonFormatBuilder.JsonType.OBJ) throw new UnsupportedOperationException("Use the #setKeyAsObjArr method for defining the format of an array of objects.");
        if (arrType == JsonFormatBuilder.JsonType.ARR) throw new UnsupportedOperationException("This function is currently not supported:\n Arrays in arrays.");

        final JsonArrTagBuilder builder = new JsonArrTagBuilder(this, arrType);
        this.keyTagMap.put(key, builder);
        return builder;
    }

    /**
     * Sets the value at the key to be an array of objects.
     *
     * @param key Key in the Json
     * @param format The format of the JsonObjects. See also {@link JsonFormatBuilder#createObjBuilder()}
     */
    public JsonObjArrTagBuilder setKeyAsObjArr(final @NotNull String key, final @NotNull JsonObjFormatBuilder format) {
        final JsonObjArrTagBuilder builder = new JsonObjArrTagBuilder(this, format);
        this.keyTagMap.put(key, builder);
        return builder;
    }




    ///////////////////////////////////////////////////////////////////////////
    // Checker
    ///////////////////////////////////////////////////////////////////////////

    public static class JsonCheckResult {
        final boolean isInFormat;
        final Map<String, JsonFormatCheckError> errorMsgs;

        JsonCheckResult(boolean isInFormat, Map<String, JsonFormatCheckError> errorMsgs) {
            this.isInFormat = isInFormat;
            this.errorMsgs = errorMsgs;
        }

        public boolean isInFormat() {
            return isInFormat;
        }

        public Map<String, JsonFormatCheckError> getErrorMsgs() {
            return errorMsgs;
        }
    }

    /**
     * Checks if the provided {@link JsonObject} follows the predefined structure.
     *
     * @param json JsonObject to check
     * @return Result of the check, containing potential errors
     */
    public JsonCheckResult checkFormat(final @NotNull JsonObject json) {
        final Map<String, JsonFormatCheckError> errorMsgs = new HashMap<>();

        for (Map.Entry<String, JsonTagBuilder> entry : keyTagMap.entrySet())
            checkTagRecursion(json, entry.getKey(), "", entry.getValue(), errorMsgs);

        final boolean empty = errorMsgs.isEmpty();
        return new JsonCheckResult(empty, errorMsgs);
    }

    private void checkTagRecursion(final JsonObject json, final String key, String path, final JsonTagBuilder tag, final Map<String, JsonFormatCheckError> errorMsgs) {
        final JsonFormatBuilder.JsonType type = tag.type;
        path = (path.isEmpty()) ? key : path + "." + key;

        if (!json.has(key) && !tag.isOptional) {
            errorMsgs.put(path, new JsonMissingTagError(path, type));
            return;
        }

        final JsonElement elem = json.get(key);
        if (elem == null) return;   // This should only ever happen if the tag is optional
        if (!type.predicate.isType(elem)) {
            errorMsgs.put(path, new JsonTypeMissmatchError(path, type, elem));
            return;
        }
        
        if (type == JsonFormatBuilder.JsonType.OBJ) {
            final JsonObjTagBuilder objTag = (JsonObjTagBuilder) tag;
            for (Map.Entry<String, JsonTagBuilder> entry : objTag.getFormat().keyTagMap.entrySet())
                checkTagRecursion(elem.getAsJsonObject(), entry.getKey(), path, entry.getValue(), errorMsgs);
        }
        else if (type == JsonFormatBuilder.JsonType.ARR) checkArrRecursion(elem.getAsJsonArray(), path, ((JsonArrTagBuilder) tag), errorMsgs);
    }

    private void checkArrRecursion(final JsonArray json, String path, final JsonArrTagBuilder tag, final Map<String, JsonFormatCheckError> errorMsgs) {
        if (tag.minSize == tag.maxSize && tag.minSize != null && json.size() != tag.minSize)
            errorMsgs.put(path, JsonIncorrectSizeError.getIncorrectSize(path, tag.minSize));

        else if (tag.minSize != null && json.size() < tag.minSize)
            errorMsgs.put(path, JsonIncorrectSizeError.getTooFew(path, tag.minSize));

        else if (tag.maxSize != null && json.size() > tag.maxSize)
            errorMsgs.put(path, JsonIncorrectSizeError.getTooMany(path, tag.minSize));

        int counter = 0;
        for (JsonElement jsonElement : json) {
            checkArrIndex(jsonElement, (path + "[" + counter + "]"), tag, errorMsgs);
            counter++;
        }
    }

    private void checkArrIndex(final JsonElement json, final String path, final JsonArrTagBuilder tag, final Map<String, JsonFormatCheckError> errorMsgs) {
        final JsonFormatBuilder.JsonType type = tag.arrType;

        if (!type.predicate.isType(json)) {
            errorMsgs.put(path, new JsonTypeMissmatchError(path, type, json));
            return;
        }

        if (type == JsonFormatBuilder.JsonType.OBJ) {
            final JsonObjArrTagBuilder objTag = (JsonObjArrTagBuilder) tag;
            for (Map.Entry<String, JsonTagBuilder> entry : objTag.getFormat().keyTagMap.entrySet())
                checkTagRecursion(json.getAsJsonObject(), entry.getKey(), path, entry.getValue(), errorMsgs);
        }
    }
}
