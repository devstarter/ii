package org.ayfaar.app.utils.speller;

import org.ayfaar.app.utils.TermsMap;
import org.languagetool.rules.*;
import org.languagetool.rules.ru.*;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static java.util.Arrays.asList;

@Component
public class Russian extends org.languagetool.language.Russian {
	@Inject TermsMap termsMap;

	@Override
	public List<Rule> getRelevantRules(ResourceBundle messages) throws IOException {
		SpellerRule spellerRule = new SpellerRule(messages, this);
		List<String> terms = new ArrayList<String>();
		for (Map.Entry<String, TermsMap.TermProvider> entry : termsMap.getAll()) {
			terms.add(entry.getKey());
		}
		spellerRule.addIgnoreTokens(terms);

		return asList(
				new CommaWhitespaceRule(messages),
				new DoublePunctuationRule(messages),
				new UppercaseSentenceStartRule(messages, this),
				spellerRule,
				new WordRepeatRule(messages, this),
				new MultipleWhitespaceRule(messages, this),
				// specific to Russian :
				new RussianUnpairedBracketsRule(messages, this),
				new RussianCompoundRule(messages),
				new RussianSimpleReplaceRule(messages),
				new RussianWordRepeatRule(messages)
		);
	}
}
