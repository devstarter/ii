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
		List<TranslationItem> secondItemList = translatedStream.collect(Collectors.toList());
		Iterator<TranslationItem> iterator = result.iterator();
		Integer lastItemRowNumber = secondItemList.get(secondItemList.size() - 1).getRowNumber().get() + 1;
		while (iterator.hasNext()) {
			TranslationItem firstItem = iterator.next();
			boolean found = false;
			for (TranslationItem secondItem : secondItemList) {
				if (firstItem.getOrigin().equals(secondItem.getOrigin())) {
					found = true;
					break;
				}
			}
			if (found) {
				iterator.remove();
			} else {
				firstItem.setRowNumber(Optional.of(lastItemRowNumber));
				lastItemRowNumber++;
			}
		}

		return result.stream();
	}
}
