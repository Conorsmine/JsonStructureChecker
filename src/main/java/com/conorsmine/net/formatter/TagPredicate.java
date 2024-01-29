package com.conorsmine.net.formatter;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

public interface TagPredicate {

    boolean isType(@NotNull JsonElement tag);


    class UIntPredicate implements TagPredicate {

        @Override
        public boolean isType(@NotNull JsonElement tag) {
            if (!tag.isJsonPrimitive()) return false;
            return tag.getAsString().matches("\\+?\\d+");
        }
    }

    class IntPredicate implements TagPredicate {

        @Override
        public boolean isType(@NotNull JsonElement tag) {
            if (!tag.isJsonPrimitive()) return false;
            return tag.getAsString().matches("[+|-]?\\d+");
        }
    }

    class UFltPredicate implements TagPredicate {

        @Override
        public boolean isType(@NotNull JsonElement tag) {
            if (!tag.isJsonPrimitive()) return false;
            return tag.getAsString().matches("\\+?\\d+(.\\d+)?");
        }
    }

    class FltPredicate implements TagPredicate {

        @Override
        public boolean isType(@NotNull JsonElement tag) {
            if (!tag.isJsonPrimitive()) return false;
            return tag.getAsString().matches("[+|-]?\\d+(.\\d+)?");
        }
    }

    class StrPredicate implements TagPredicate {

        @Override
        public boolean isType(@NotNull JsonElement tag) {
            return tag.isJsonPrimitive();
        }
    }

    class ObjPredicate implements TagPredicate {

        @Override
        public boolean isType(@NotNull JsonElement tag) {
            return tag.isJsonObject();
        }
    }

    class ArrPredicate implements TagPredicate {

        @Override
        public boolean isType(@NotNull JsonElement tag) {
            return tag.isJsonArray();
        }
    }
}

