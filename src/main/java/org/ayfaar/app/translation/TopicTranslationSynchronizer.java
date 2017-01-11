package org.ayfaar.app.translation;

import org.ayfaar.app.services.topics.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Stream;

@Service
public class TopicTranslationSynchronizer {
	@Autowired
	TopicService topicService;
	@Autowired
	GoogleSpreadsheetTranslator googleSpreadsheetTranslator;
	@Autowired
	TranslationComparator translationComparator;

	public void firstUpload() {
		googleSpreadsheetTranslator.setBaseRange("Topic");
		Stream<TranslationItem> originItems= topicService.getAllNames().stream().map(TranslationItem::new);
		Stream<TranslationItem> translationItems = Stream.empty();
		Stream<TranslationItem> items = translationComparator.getNotUploadedOrigins(originItems, translationItems);
		googleSpreadsheetTranslator.write(items);
	}

	public void synchronize() {
		googleSpreadsheetTranslator.setBaseRange("Topic");
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
