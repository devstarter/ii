package org.ayfaar.app.translation;

import org.apache.commons.lang3.tuple.Pair;
import org.ayfaar.app.enums.SyncType;

import java.util.stream.Stream;

public class TranslationComparator {
	public Stream<Pair<SyncType, TranslationItem>> compare(
			Stream<TranslationItem> itemsDB, Stream<TranslationItem> itemsRemoteService) {
		return null;
	}
}
