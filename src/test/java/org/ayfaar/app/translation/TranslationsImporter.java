package org.ayfaar.app.translation;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.Pair;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Translation;
import org.ayfaar.app.services.TranslationService;
import org.ayfaar.app.utils.Language;
import org.junit.Test;

import javax.inject.Inject;
import java.io.File;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.UNICODE_CASE;
import static java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;
import static org.apache.commons.io.FileUtils.readLines;

@Slf4j
public class TranslationsImporter extends IntegrationTest {
	@Inject CommonDao commonDao;
	@Inject TranslationService service;

	@Test
	public void main() throws Exception {
		StreamEx.of(readLines(new File("D:\\PROJECTS\\ayfaar\\ii-app\\src\\test\\resources\\translation\\!CurrentDatabase-Russian-English-4-IISSIIDIOLOGY-translation-Sorted-by-number-of-words-down.txt"), Charset.forName("UTF-8")))
				.map(l -> l.split(";"))
				.map(a -> Pair.of(a[0], cleanTranslation(a[1])))
				.map(p -> new Translation(p.getKey(), p.getValue(), Language.en))
				.forEachOrdered(t -> commonDao.save(t));
		;

	}

	private String cleanTranslation(String s) {
		s = s.replaceAll("^=(.+)$", "$1");
		s = s.replaceAll("^(.+)-И$", "$1");
		s = s.replaceAll("^(.+)\u007F$", "$1");
		s = s.replaceAll("^(.+)\\?$", "$1");
		s = Pattern.compile("^(.+)(-|(-[а-я]+(!+)?)|(\\++)|(!+))$", UNICODE_CHARACTER_CLASS | UNICODE_CASE)
				.matcher(s).replaceAll("$1");
		return s;
	}


}
