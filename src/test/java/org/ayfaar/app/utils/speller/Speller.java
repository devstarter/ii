package org.ayfaar.app.utils.speller;

import org.ayfaar.app.dao.ItemDao;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Profile("speller")
public class Speller {
	@Inject Russian russian;
	@Inject ItemDao itemDao;

	public List<RuleMatch> check(String text) throws IOException {
		if (text == null || text.isEmpty()) return null;
		JLanguageTool langTool = new JLanguageTool(russian);
		langTool.disableRule("AllCaps");
		langTool.disableCategory("Стиль");
		return langTool.check(text);
	}

	public Object checkItem(@PathVariable String item) throws IOException {
		String content = itemDao.getByNumber(item).getContent();
		JLanguageTool langTool = new JLanguageTool(russian);
		langTool.disableRule("AllCaps");
		langTool.disableCategory("Стиль");
		List<RuleMatch> matches = langTool.check(content);
		if (matches == null || matches.isEmpty()) return  "OK";

		List<Map> result = new ArrayList<Map>();

		for (RuleMatch match : matches) {
			ModelMap map = new ModelMap();
			map.put("rule", match.getRule().getId());
			map.put("message", match.getMessage());
			map.put("text", content.substring(match.getFromPos(), match.getToPos()));
			result.add(map);
		}

		return result;
	}

	public String consoleCheck(String text) throws IOException {
		if (text == null || text.isEmpty()) return text;
		text = text.replaceAll("\\s+", " ");
		/*List<RuleMatch> checkResult = check(text);
		if (checkResult != null && !checkResult.isEmpty()) {
			System.out.println("Контекст: "+text);
			for (RuleMatch match : checkResult) {
				System.out.println(format("Error: %s in \n%s\n", match.getMessage(), text.substring(match.getFromPos(), match.getToPos())));
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				if (!match.getSuggestedReplacements().isEmpty()) {
					for (int i = 0; i < match.getSuggestedReplacements().size(); i++) {
						System.out.println(format("# Предложение %s: %s", i, match.getSuggestedReplacements().get(i)));
					}
				}
				System.out.println("# пусто: игнорировать ошибку");
				System.out.println("# или просто введите свой вариант");
				String input = br.readLine();
				if (input.isEmpty()) continue;
				String replacement = "";
				if (!input.matches("^\\d+$")) {
					replacement = input;
				} else {
					try {
						int i = Integer.parseInt(input);
						replacement = match.getSuggestedReplacements().get(i);
					} catch (NumberFormatException nfe) {
						System.err.println("Invalid input!");
					}
				}
				text = text.substring(0, match.getFromPos()) + replacement + text.substring(match.getToPos(), text.length());
			}
		}*/
		return text;
	}
}
