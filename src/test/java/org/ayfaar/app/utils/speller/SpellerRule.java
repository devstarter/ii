package org.ayfaar.app.utils.speller;

import org.languagetool.Language;
import org.languagetool.rules.spelling.morfologik.MorfologikSpellerRule;

import java.io.IOException;
import java.util.ResourceBundle;

public class SpellerRule extends MorfologikSpellerRule {

	public static final String RULE_ID = "MORFOLOGIK_RULE_RU_RU";

	private static final String RESOURCE_FILENAME = "/ru/hunspell/ru_RU.dict";

	public SpellerRule(ResourceBundle messages, Language language) throws IOException {
		super(messages, language);
		setCheckCompound(true);
		setConvertsCase(false);
	}

	@Override
	public String getFileName() {
		return "/dict/ru_RU.dict";
	}

	@Override
	protected String getSpellingFileName() {
		return "/dict/spelling.txt";
	}

	@Override
	public String getId() {
		return RULE_ID;
	}



}
