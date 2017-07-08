package org.ayfaar.app.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TermRenameHelper {
    public static Optional<RenameSuggestion> suggestRename(@NotNull String term, @Nullable String messsage) {
        return Optional.empty();
    }

    public static class RenameSuggestion {
        @NotNull
        public String name;
        @Nullable
        public String message;
    }
}