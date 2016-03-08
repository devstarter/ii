package org.ayfaar.app.services;

import java.util.Optional;

public interface TopicService {
    Optional<TopicProvider> get(String uri);
    TopicProvider getOrCreate(String name);
}
