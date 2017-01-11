package org.ayfaar.app.translation;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class TranslationComparatorTest {
	private final TranslationComparator translationComparator = new TranslationComparator();

	@Test
	public void testGetFirstNotInSecond() throws Exception {
		TranslationItem item1 = new TranslationItem(Optional.of(1), "or1", "tr");
		TranslationItem item2 = new TranslationItem(Optional.of(2), "or2", "tr");
		TranslationItem item3 = new TranslationItem(Optional.of(3), "or3", "tr");
		TranslationItem item4 = new TranslationItem(Optional.of(4), "or4", "tr");
		TranslationItem item5 = new TranslationItem(Optional.of(5), "or5", "tr");

		List<TranslationItem> itemsOriginal = Arrays.asList(item1, item2, item3);
		List<TranslationItem> itemsToExclude = Arrays.asList(item2, item4, item5);

		Stream<TranslationItem> result =
				translationComparator.getNotUploadedOrigins(itemsOriginal.stream(), itemsToExclude.stream());

		Stream<TranslationItem> expected = Arrays.asList(item1, item3).stream();
		assertThat(expected.collect(Collectors.toList()), Matchers.containsInAnyOrder(result.toArray()));
	}
}
