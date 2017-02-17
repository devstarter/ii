package org.ayfaar.app.translation;

import org.ayfaar.app.event.SysLogEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TranslationComparator {
    private ApplicationEventPublisher publisher;

    @Autowired
    public TranslationComparator(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

	public Stream<TranslationItem> getNotUploadedOrigins(Stream<TranslationItem> originStream, Stream<TranslationItem> translatedStream) {
		List<TranslationItem> fromGoogle = translatedStream.collect(Collectors.toList());
		final AtomicInteger lastRowNumber = new AtomicInteger(fromGoogle.stream()
				.reduce((a, b) -> b).orElse(new TranslationItem(Optional.of(0))).getRowNumber().get() + 1);
		return originStream
				.flatMap(originItem -> fromGoogle.parallelStream()
						.anyMatch(translatedItem -> originItem.getOrigin().equals(translatedItem.getOrigin()))
							? Stream.empty() : Stream.of(originItem))
				.peek(item -> item.setRowNumber(Optional.of(lastRowNumber.getAndIncrement())));
	}

	public Stream<TranslationItem> getNotDownloadedTranslations(Stream<TranslationItem> translatedItemsGoogle,
																Stream<TranslationItem> translatedItemsDB) {
		List<TranslationItem> fromDB = translatedItemsDB.collect(Collectors.toList());
		return translatedItemsGoogle
				.filter(t -> !t.getTranslation().isEmpty())
				.flatMap(itemGoogle -> {
					AtomicBoolean originSynced = new AtomicBoolean(false);
					Optional<TranslationItem> syncedOriginNonSyncedTranslation =  fromDB.parallelStream()
							.filter(itemDB -> {
								if (originSynced.get()) {
									return false;
								}
								if (itemDB.getOrigin().equals(itemGoogle.getOrigin())) {
									originSynced.set(true);
									return !itemDB.getTranslation().equals(itemGoogle.getTranslation());
								}
								return false;
							}).findAny();
					return syncedOriginNonSyncedTranslation.isPresent() || !originSynced.get()
							? Stream.of(itemGoogle) : Stream.empty();
				});
	}

	public Stream<TranslationItem> removeIfNotInTopics(Stream<TranslationItem> originItems, Stream<TranslationItem> translatedItemsGoogle) {
        List<TranslationItem> originAsList = originItems.collect(Collectors.toList());
        List<TranslationItem> notInOrigins = new ArrayList<>();
        List<TranslationItem> resultList = translatedItemsGoogle
                .flatMap(itemGoogle -> {
                    boolean isPresent = originAsList.parallelStream().anyMatch(itemOrigin -> itemGoogle.getOrigin().equals(itemOrigin.getOrigin()));
                    if (isPresent) {
                        return Stream.of(itemGoogle);
                    } else {
                        notInOrigins.add(itemGoogle);
                        return Stream.empty();
                    }
                }).collect(Collectors.toList());

        if (!notInOrigins.isEmpty()) {
            publisher.publishEvent(new SysLogEvent(this, "Items in Google Spreadsheet doesn't exist in Topic table: "
                    + notInOrigins.toString(), LogLevel.WARN));
        }

        return resultList.stream();
	}
}
