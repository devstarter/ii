package org.ayfaar.app.translation;

import lombok.*;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TranslationItem {
	private Optional<Integer> rowNumber = Optional.empty();
	private String origin = "";
	private String translation = "";

	public TranslationItem(String origin) {
		this.origin = origin;
	}

	public TranslationItem(Optional<Integer> rowNumber) {
		this.rowNumber = rowNumber;
	}

	public TranslationItem(String origin, String translation) {
		this.origin = origin;
		this.translation = translation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TranslationItem item = (TranslationItem) o;

		if (rowNumber != null ? !rowNumber.equals(item.rowNumber) : item.rowNumber != null) return false;
		if (origin != null ? !origin.equals(item.origin) : item.origin != null) return false;
		return !(translation != null ? !translation.equals(item.translation) : item.translation != null);

	}

	@Override
	public int hashCode() {
		int result = rowNumber != null ? rowNumber.hashCode() : 0;
		result = 31 * result + (origin != null ? origin.hashCode() : 0);
		result = 31 * result + (translation != null ? translation.hashCode() : 0);
		return result;
	}
}
