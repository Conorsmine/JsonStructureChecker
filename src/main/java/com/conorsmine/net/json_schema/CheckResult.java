package com.conorsmine.net.json_schema;

import com.conorsmine.net.json_schema.errors.JsonFormatCheckError;
import com.conorsmine.net.json_schema.tags.JsonTag;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CheckResult {

    private final Map<String, JsonFormatCheckError> errorMsgs;

    CheckResult(Map<String, JsonFormatCheckError> errorMsgs) {
        this.errorMsgs = Collections.unmodifiableMap(errorMsgs.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll));
    }

    public boolean isValid() {
        return errorMsgs.isEmpty();
    }

    public Map<String, JsonFormatCheckError> getErrorMsgs() {
        return errorMsgs;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builder
    ///////////////////////////////////////////////////////////////////////////

    public static class Builder {

        private final Map<String, JsonTag> groupMap;
        private final Map<String, JsonFormatCheckError> errorMsgs = new LinkedHashMap<>();

        Builder(Map<String, JsonTag> groupMap) {
            this.groupMap = groupMap;
        }

        public Builder addError(String key, JsonFormatCheckError error) {
            errorMsgs.put(key, error);
            return this;
        }

        public CheckResult build() {
            return new CheckResult(errorMsgs);
        }

        public Map<String, JsonTag> getGroupMap() {
            return groupMap;
        }
    }
}
