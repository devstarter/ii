package org.ayfaar.app.translation;

//import org.ayfaar.app.event.EventPublisher;
//import org.junit.Test;
//import org.springframework.context.ApplicationEvent;
//import org.springframework.context.ApplicationEventPublisher;
//
//import java.util.*;
//import java.util.stream.Stream;
//
//import static org.hamcrest.Matchers.containsInAnyOrder;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThat;

public class TranslationComparatorTest {
//	private final TranslationComparator translationComparator = new TranslationComparator(new EventPublisher());
//
//	@Test
//	public void testGetNotUploadedOrigins() throws Exception {
//		TranslationItem item1 = new TranslationItem(Optional.of(1), "or1", "tr");
//		TranslationItem item2 = new TranslationItem(Optional.of(2), "or2", "tr");
//		TranslationItem item3 = new TranslationItem(Optional.of(3), "or3", "tr");
//		TranslationItem item4 = new TranslationItem(Optional.of(4), "or4", "tr");
//		TranslationItem item5 = new TranslationItem(Optional.of(5), "or5", "tr");
//
//		List<TranslationItem> itemsOriginal = Arrays.asList(item1, item2, item3);
//		List<TranslationItem> itemsToExclude = Arrays.asList(item2, item4, item5);
//
//		Stream<TranslationItem> resultStream =
//				translationComparator.getNotUploadedOrigins(itemsOriginal.stream(), itemsToExclude.stream());
//
//		TranslationItem[] result = resultStream.toArray(TranslationItem[]::new);
//		List<TranslationItem> expected = Arrays.asList(item1, item3);
//		assertEquals(expected.size(), result.length);
//		assertThat(expected, containsInAnyOrder(result));
//		Integer firstRowNumber = 6;
//		for (TranslationItem item : result) {
//			assertEquals(firstRowNumber, item.getRowNumber().get());
//			firstRowNumber++;
//		}
//
//
////		List<TranslationItem> expected = Arrays.asList(item1, item3);
////		List<TranslationItem> others = itemsOriginal.stream()
////				.filter(i -> !itemsToExclude.contains(i))
////				.collect(Collectors.toList());
////
////		assertThat(expected, containsInAnyOrder(others.toArray()));
//	}
}
