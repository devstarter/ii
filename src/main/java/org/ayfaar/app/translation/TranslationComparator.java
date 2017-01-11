package org.ayfaar.app.translation;

import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TranslationComparator {
	public Stream<TranslationItem> getNotUploadedOrigins(Stream<TranslationItem> originStream, Stream<TranslationItem> translatedStream) {
		List<TranslationItem> result = originStream.collect(Collectors.toList());
		List<TranslationItem> translatedList = translatedStream.collect(Collectors.toList());
		Integer lastItemRowNumber = 0;
		if (translatedList.size() != 0) {
			lastItemRowNumber = translatedList.get(translatedList.size() - 1).getRowNumber().get() + 1;
		}
		Iterator<TranslationItem> iterator = result.iterator();
		while (iterator.hasNext()) {
			TranslationItem originItem = iterator.next();
			// TODO this doesn't work. Can't use lastItemRowNumber inside of the map(...)
//			translatedList.stream()
//					.filter(translatedItem -> ! originItem.getOrigin().equals(translatedItem.getOrigin()))
//					.map(translatedItem -> {
//						translatedItem.setRowNumber(Optional.of(lastItemRowNumber));
//						lastItemRowNumber++;
//						return translatedItem;
//					});
			boolean found = false;
			for (TranslationItem translatedItem : translatedList) {
				if (originItem.getOrigin().equals(translatedItem.getOrigin())) {
					found = true;
					break;
				}
			}
			if (found) {
				iterator.remove();
			} else {
				originItem.setRowNumber(Optional.of(lastItemRowNumber));
				lastItemRowNumber++;
			}
		}

		return result.stream();
	}
}
