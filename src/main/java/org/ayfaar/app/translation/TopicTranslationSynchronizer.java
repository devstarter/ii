package org.ayfaar.app.translation;

import org.ayfaar.app.model.Translation;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.services.translations.TranslationService;
import org.ayfaar.app.utils.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class TopicTranslationSynchronizer {
	TopicService topicService;
	GoogleSpreadsheetTranslator googleSpreadsheetTranslator;
	TranslationComparator translationComparator;
	TranslationService translationService;

	@Autowired
	public TopicTranslationSynchronizer(TopicService topicService, GoogleSpreadsheetTranslator translator,
										TranslationComparator comparator, TranslationService translationService) {
		this.topicService = topicService;
		translator.setSheetName("Topic");
		this.googleSpreadsheetTranslator = translator;
		this.translationComparator = comparator;
		this.translationService = translationService;
	}

	public void firstUpload() {
		Stream<TranslationItem> itemsTopic = topicService.getAllNames().stream().map(TranslationItem::new);
		Stream<TranslationItem> notUploadedTopics = translationComparator.getNotUploadedOrigins(itemsTopic, Stream.empty());
		googleSpreadsheetTranslator.write(notUploadedTopics);
	}

	public void synchronize() {
		Stream<TranslationItem> itemsTopic = topicService.getAllNames().stream().map(TranslationItem::new);
		Stream<TranslationItem> itemsGoogle = googleSpreadsheetTranslator.read();
		Stream<TranslationItem> itemsTranslation = translationService.getAllAsTranslationItem();
//		// topic -> spreadsheet
		translationComparator.getNotUploadedOrigins(itemsTopic, itemsGoogle)
				.forEach(googleSpreadsheetTranslator::write);
		// spreadsheet -> translation
		Stream<TranslationItem> notDownloadedTranslations =
				translationComparator.getNotDownloadedTranslations(itemsGoogle, itemsTranslation);
		notDownloadedTranslations = translationComparator.removeIfNotInTopics(itemsTopic, notDownloadedTranslations);
		notDownloadedTranslations
				.map(item -> new Translation(item.getOrigin(), item.getTranslation(), Language.en))
				.forEach(translationService::save);
	}

	protected void applyChangesToDb(Stream<TranslationItem> items) {

	}

	protected void applyChangesToRemoteService(Stream<TranslationItem> items) {

	}
}
