package org.ayfaar.app.translation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.ayfaar.app.event.EventPublisher;
import org.ayfaar.app.event.SysLogEvent;
import org.ayfaar.app.model.Translation;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.services.translations.TranslationService;
import org.ayfaar.app.utils.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ayfaar.app.services.moderation.Action.SYSLOG_TRANSLATION_NEW;
import static org.ayfaar.app.services.moderation.Action.SYSLOG_TRANSLATION_UPDATE;
import static org.slf4j.helpers.MessageFormatter.arrayFormat;

@Slf4j
@EnableScheduling
@Service
public class TopicTranslationSynchronizer {
    private static final String NO_UPDATES = "Synchronization: no updates";
    private static final String PREFIX_NEW_TRANSLATION = "New translations for: ";
    private static final String PREFIX_UPDATED_TRANSLATION = "Updated translations for: ";

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
	private TopicService topicService;
	private GoogleSpreadsheetTranslator googleSpreadsheetTranslator;
	private TranslationComparator translationComparator;
	private TranslationService translationService;
    private final EventPublisher publisher;

    @Autowired
	public TopicTranslationSynchronizer(TopicService topicService,
                                        GoogleSpreadsheetTranslator translator,
                                        TranslationComparator comparator,
                                        TranslationService translationService,
                                        EventPublisher publisher) {
		this.topicService = topicService;
		this.googleSpreadsheetTranslator = translator;
		this.translationComparator = comparator;
		this.translationService = translationService;
        this.publisher = publisher;
    }

    @Scheduled(cron = "0 0 1 * * ?") // at 1 AM every day
    public void synchronize() {
        log.info("Topic translation sync started {}", dateFormat.format(new Date()));

        // get data
        List<TranslationItem> itemsTopicList = topicService.getAllNames().stream().map(TranslationItem::new).collect(Collectors.toList());
        Supplier<Stream<TranslationItem>> itemsTopicSupplier = itemsTopicList::stream;
        List<TranslationItem> itemsGoogleList = googleSpreadsheetTranslator.read().collect(Collectors.toList());
        Supplier<Stream<TranslationItem>> itemsGoogleSupplier = itemsGoogleList::stream;
        List<TranslationItem> itemsTranslationList = translationService.getAllAsTranslationItem().collect(Collectors.toList());
        Supplier<Stream<TranslationItem>> itemsTranslationSupplier = itemsTranslationList::stream;
        // topic -> spreadsheet
        googleSpreadsheetTranslator.write(translationComparator.getNotUploadedOrigins(itemsTopicSupplier.get(), itemsGoogleSupplier.get()));
        // spreadsheet -> translation
        Stream<TranslationItem> notDownloadedTranslations =
                translationComparator.getNotDownloadedTranslations(itemsGoogleSupplier.get(), itemsTranslationSupplier.get());
        notDownloadedTranslations = translationComparator.removeIfNotInTopics(itemsTopicSupplier.get(), notDownloadedTranslations);
        List<TranslationItem> notDownloadedTranslationsList = notDownloadedTranslations.collect(Collectors.toList());
        Supplier<Stream<TranslationItem>> notDownloadedTranslationsSupplier = notDownloadedTranslationsList::stream;
        notDownloadedTranslationsSupplier.get()
                .map(item -> new Translation(item.getOrigin(), item.getTranslation(), Language.en))
                .forEach(translationService::save);

        log.info("Topic translation sync finished {}", dateFormat.format(new Date()));
        logTranslationInfoToDb(notDownloadedTranslationsList, itemsTranslationSupplier.get());
    }

    private void logTranslationInfoToDb(List<TranslationItem> notDownloadedTranslationsList, Stream<TranslationItem> itemsTranslated) {
        List<TranslationItem> itemTranslatedList = itemsTranslated.collect(Collectors.toList());
        final AtomicReference<String> messageUpdateAtomic = new AtomicReference<>("");
        final AtomicReference<String> messageNewAtomic = new AtomicReference<>("");
        notDownloadedTranslationsList.stream()
                .forEach(notDownloaded -> {
                    Optional<TranslationItem> foundTranslation = itemTranslatedList.stream()
                            .filter(t -> notDownloaded.getOrigin().equals(t.getOrigin()) && !t.getTranslation().isEmpty())
                            .findAny();
                    if (foundTranslation.isPresent()) {
                        messageUpdateAtomic.set(messageUpdateAtomic.get() +
                                arrayFormat(SYSLOG_TRANSLATION_UPDATE.message, new Object[]{notDownloaded.getOrigin(),
                                        foundTranslation.get().getTranslation(), notDownloaded.getTranslation()})
                                        .getMessage());
                    } else {
                        messageNewAtomic.set(messageNewAtomic.get() +
                                arrayFormat(SYSLOG_TRANSLATION_NEW.message, new Object[]{notDownloaded.getOrigin(),
                                        notDownloaded.getTranslation()}).getMessage());
                    }
                });
        String messageNewTranslation = messageNewAtomic.get();
        String messageUpdatedTranslation = messageUpdateAtomic.get();
        String message = StringUtils.EMPTY;
        if (StringUtils.isNotEmpty(messageNewTranslation)) {
            messageNewTranslation = messageNewTranslation.substring(0, messageNewTranslation.length()-2);
            messageNewTranslation = PREFIX_NEW_TRANSLATION + messageNewTranslation;
            message = messageNewTranslation + ". ";
        }
        if (StringUtils.isNotEmpty(messageUpdatedTranslation)) {
            messageUpdatedTranslation = messageUpdatedTranslation.substring(0, messageUpdatedTranslation.length()-2);
            messageUpdatedTranslation = PREFIX_UPDATED_TRANSLATION + messageUpdatedTranslation;
            message = message + messageUpdatedTranslation + ".";
        }
        if (StringUtils.isEmpty(message)) {
            message = NO_UPDATES;
        }
        publisher.publishEvent(new SysLogEvent(this, message, LogLevel.INFO));
    }
}
