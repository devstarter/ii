package org.ayfaar.app.contents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.utils.ContentsService;
import org.ayfaar.app.utils.ContentsService.ContentsProvider;
import org.ayfaar.app.utils.ContentsService.ParagraphProvider;
import org.ayfaar.app.utils.contents.CategoryPresentation;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.inject.Inject;
import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

@Ignore
public class ContentsGenerator extends IntegrationTest {
	private static final Logger logger = LoggerFactory.getLogger(ContentsGenerator.class);

	@Inject ContentsService contentsService;
	@Inject TemplateEngine templateEngine;

	@Test
	public void main() throws Exception {
		// переменные видимые в шаблоне
		Map<String, Object> values = new HashMap<>();
		// провейдер категории "Том 4"
		Optional<? extends ContentsProvider> rootCategoryProviderOpt = contentsService.get("Том 15");
		ContentsService.CategoryProvider rootCategoryProvider = (ContentsService.CategoryProvider) rootCategoryProviderOpt.get();
		logger.trace("Загруженная категория: " + rootCategoryProvider.extractCategoryName());
		// в этом объект нужно положить всю нужную в шаблоне информацию о категрии (имя, описание, дочерние категории...)
		CategoryPresentation rootCategoryPresentation = null; //
		List<CategoryPresentation> categoryPresentationList = fillCategoryPresentationList(rootCategoryProvider.children());
		// заполняем поле name, которое содержит полное название (к примеру - Основы. Том 4)
		String rootCategoryPresentationName;
		rootCategoryPresentationName = rootCategoryProvider.getParentUri().substring(rootCategoryProvider.getParentUri().indexOf(":")+1)+"."+rootCategoryProvider.extractCategoryName();
		rootCategoryPresentation =  new CategoryPresentation(rootCategoryPresentationName,
				rootCategoryProvider.uri(), rootCategoryProvider.description(), categoryPresentationList);
		// делаем объект категории доступным для шаблона по пути "data"
		values.put("data", rootCategoryPresentation);
		// заполняем шаблон данными и получаем результат в виде html с данными
		String html = templateEngine.process("contents.html", new Context(Locale.getDefault(), values));
//		logger.trace("Результат работы шаблонизатора: \n" + html);
		logger.trace("Генерация шаблона окончена.");
		// записываем полученный результат в html файл для просмотра его в браузере.
		FileUtils.writeStringToFile(new File(rootCategoryProvider.name()+".html"), html, Charset.forName("UTF-8"));
	}


	/**
	 * Рекурсивно проходим все дерево объектов, находящихся в корневом объекте,
	 * и заполняем список. После эти данные можно передавать на движок генерации
	 * html - страниц.
	 * @param rootCategoryProvider родительский объект класса CategoryProvider,
	 * из детей которого получаем список.
     * @return ArrayList, содержащий все объекты класса CategoryPresentation
	 */
	public static List<CategoryPresentation> fillCategoryPresentationList(List<? extends ContentsProvider> rootCategoryProvider){
		// рекурсивно заполняем список сategoryPresentationList
		List<CategoryPresentation> сategoryPresentationList = new ArrayList<>();
		for (ContentsProvider provider : rootCategoryProvider){
			// заполняем поле name, которое содержит название параграфа, главы и т.д.
			String name = null;
			String paragraphCode = null;
			if (provider instanceof ContentsService.CategoryProvider) {
				name = ((ContentsService.CategoryProvider) provider).extractCategoryName();
			}
			if (provider instanceof ParagraphProvider) {
			 	paragraphCode = provider.code();
				name = paragraphCode.replaceAll("^\\d+\\.\\d+\\.(\\d+\\.\\d+)$", "$1"); // оставляем только две последние цифры
			}
			// рекурсивно заполняем список, со всеми вложенными детьми
			сategoryPresentationList.add(new CategoryPresentationExt(name, provider.startItemNumber(),
					provider.description(),
					provider instanceof ContentsService.CategoryProvider
							? fillCategoryPresentationList(((ContentsService.CategoryProvider) provider).children())
							: Collections.emptyList(),
					paragraphCode));
		}
		return сategoryPresentationList;
	}

	/**
	* Этот класс создан для того, что бы формировался
	* полноценный путь для перехода на сайт, на конкретное
	* место. Добавленна ссылка code
	*/
	@Data
	@NoArgsConstructor
	public static class CategoryPresentationExt extends CategoryPresentation {
		/**
		 * Ссылка на место в источнике
		 */
		private String code;
		/**
		 * Здесь мы задаем полную ссылку на место в документации,
		 * с помощью которой формируем путь на сайте. <br>
		 * К примеру - 4.13.1.3
		*/
		public CategoryPresentationExt(String name, String uri, String description, List<CategoryPresentation> children, String code) {
			super(name, uri, description, children);
			this.code = code;
		}


	}
}
