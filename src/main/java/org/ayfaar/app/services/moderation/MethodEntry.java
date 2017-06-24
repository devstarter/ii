package org.ayfaar.app.services.moderation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MethodEntry {
    @NonNull public Action action;
    @NonNull public String command;
    @NonNull public Object[] args;
}
