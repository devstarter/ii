package org.ayfaar.app.translation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationItem {
	private Optional<Integer> rowNumber;
	private String origin;
	private String translation;
}
