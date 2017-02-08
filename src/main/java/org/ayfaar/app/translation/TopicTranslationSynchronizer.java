package org.ayfaar.app.translation;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.model.Translation;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.services.translations.TranslationService;
import org.ayfaar.app.utils.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class TopicTranslationSynchronizer {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
	private TopicService topicService;
	private GoogleSpreadsheetTranslator googleSpreadsheetTranslator;
	private TranslationComparator translationComparator;
	private TranslationService translationService;

    @Autowired
	public TopicTranslationSynchronizer(TopicService topicService,
                                        GoogleSpreadsheetTranslator translator,
                                        TranslationComparator comparator,
                                        TranslationService translationService) {
		this.topicService = topicService;
		this.googleSpreadsheetTranslator = translator;
		this.translationComparator = comparator;
		this.translationService = translationService;
    }

    public void synchronize() {
        log.info("Topic translation sync started {}", dateFormat.format(new Date()));

        // get data
        List<TranslationItem> itemsTopicList = topicService.getAllNames().stream().map(TranslationItem::new).collect(Collectors.toList());
        Supplier<Stream<TranslationItem>> itemsTopicSupplier = itemsTopicList::stream;
        TranslationItem[] itemsGoogleArray = googleSpreadsheetTranslator.read().toArray(TranslationItem[]::new);
        Supplier<Stream<TranslationItem>> itemsGoogleSupplier = () -> Stream.of(itemsGoogleArray);
        Stream<TranslationItem> itemsTranslation = translationService.getAllAsTranslationItem();
        // topic -> spreadsheet
        googleSpreadsheetTranslator.write(translationComparator.getNotUploadedOrigins(itemsTopicSupplier.get(), itemsGoogleSupplier.get()));
        // spreadsheet -> translation
        Stream<TranslationItem> notDownloadedTranslations =
                translationComparator.getNotDownloadedTranslations(itemsGoogleSupplier.get(), itemsTranslation);
        notDownloadedTranslations = translationComparator.removeIfNotInTopics(itemsTopicSupplier.get(), notDownloadedTranslations);
        notDownloadedTranslations
                .map(item -> new Translation(item.getOrigin(), item.getTranslation(), Language.en))
                .forEach(translationService::save);

        log.info("Topic translation sync finished {}", dateFormat.format(new Date()));
    }
}
