package org.ayfaar.app.translation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationItem {
	private Optional<Integer> rowNumber = Optional.empty();
	private String origin;
	private String translation = "";

	public TranslationItem(String origin) {
		this.origin = origin;
	}
}
