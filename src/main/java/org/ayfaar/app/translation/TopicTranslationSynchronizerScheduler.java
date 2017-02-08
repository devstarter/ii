package org.ayfaar.app.translation;

import org.ayfaar.app.services.translations.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
@Profile("!dev")
public class TopicTranslationSynchronizerScheduler {
    TopicTranslationSynchronizer topicTranslationSynchronizer;

    @Autowired
    public TopicTranslationSynchronizerScheduler(TopicTranslationSynchronizer topicTranslationSynchronizer) {
        this.topicTranslationSynchronizer = topicTranslationSynchronizer;
    }

	private TranslationService translationService;

    @Scheduled(cron = "0 0 1 * * ?") // at 1 AM every day
    public void synchronize() {
        topicTranslationSynchronizer.synchronize();
    }
}
