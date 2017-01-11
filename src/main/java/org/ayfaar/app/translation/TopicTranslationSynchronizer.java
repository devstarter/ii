package org.ayfaar.app.translation;

import org.ayfaar.app.services.topics.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Stream;

@Service
public class TopicTranslationSynchronizer {
	TopicService topicService;
	GoogleSpreadsheetTranslator googleSpreadsheetTranslator;
	TranslationComparator translationComparator;

	@Autowired
	public TopicTranslationSynchronizer(TopicService service, GoogleSpreadsheetTranslator translator,
										TranslationComparator comparator) {
		this.topicService = service;
		translator.setSheetName("Topic");
		this.googleSpreadsheetTranslator = translator;
		this.translationComparator = comparator;
	}

	public void firstUpload() {
		Stream<TranslationItem> originItems= topicService.getAllNames().stream().map(TranslationItem::new);
		Stream<TranslationItem> translationItems = Stream.empty();
		Stream<TranslationItem> items = translationComparator.getNotUploadedOrigins(originItems, translationItems);
		googleSpreadsheetTranslator.write(items);
	}

	public void synchronize() {
		// topic -> spreadsheet
		Stream<TranslationItem> originItems= topicService.getAllNames().stream().map(TranslationItem::new);
		Stream<TranslationItem> translationItems = googleSpreadsheetTranslator.read();
		translationComparator.getNotUploadedOrigins(originItems, translationItems)
				.forEach(googleSpreadsheetTranslator::write);

		// spreadsheet -> translation
	}

	protected void applyChangesToDb(Stream<TranslationItem> items) {

	}

	protected void applyChangesToRemoteService(Stream<TranslationItem> items) {

	}
}
