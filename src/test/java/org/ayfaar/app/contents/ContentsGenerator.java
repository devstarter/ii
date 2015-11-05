package org.ayfaar.app.contents;

import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.utils.CategoryService;
import org.ayfaar.app.utils.contents.CategoryPresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ContentsGenerator {
	private static final Logger logger = LoggerFactory.getLogger(ContentsGenerator.class);

	public static void main(String[] args) throws Exception {
		// задаём профайл для загрузки только нужных бинов
		System.setProperty("spring.profiles.active", ContentsGeneratorConfig.CONTEXT_GENERATOR_PROFILE);
		// создаём контекст тестового окружения
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
		// получаем сервис для работы с категориями
		CategoryService categoryService = ctx.getBean(CategoryService.class);
		// движок html шаблонов. Про диалект шаблонов: http://www.thymeleaf.org/doc/articles/standarddialect5minutes.html
		TemplateEngine templateEngine = ctx.getBean(TemplateEngine.class);

		// переменные видимые в шаблоне
		Map<String, Object> values = new HashMap<>();
		// провейдер категории "Том 4"
		CategoryService.CategoryProvider rootCategoryProvider = categoryService.getByName("Том 4");
		logger.trace("Загруженная категория: " + rootCategoryProvider.extractCategoryName());
		// в этом объект нужно положить всю нужную в шаблоне информацию о категрии (имя, описание, дочерние категории...)
		CategoryPresentation rootCategoryPresentation = null; //
		// делаем объект категории доступным для шаблона по пути "data"
		values.put("data", rootCategoryPresentation);
		// заполняем шаблон данными и получаем результат в виде html с данными
		String html = templateEngine.process("contents.html", new Context(Locale.getDefault(), values));
		logger.trace("Результат работы шаблонизатора: \n" + html);
		// записываем полученный результат в html файл для просмотра его в браузере.
		//...
	}
}
