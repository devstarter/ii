package org.ayfaar.app.contents;

import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.utils.CategoryService;
import org.ayfaar.app.utils.contents.CategoryPresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sun.reflect.Reflection;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.*;

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
//		CategoryService.CategoryProvider rootCategoryProvider = categoryService.getByName("Том 4");
//		CategoryService.CategoryProvider rootCategoryProvider = categoryService.getByName("параграф:1.1.1.1");
		CategoryService.CategoryProvider rootCategoryProvider = categoryService.getByName("Том 4");
		logger.trace("Загруженная категория: " + rootCategoryProvider.extractCategoryName());
		// в этом объект нужно положить всю нужную в шаблоне информацию о категрии (имя, описание, дочерние категории...)
		CategoryPresentation rootCategoryPresentation = null; //
		List<CategoryPresentation> categoryPresentationList = new ArrayList<>();
		for (CategoryService.CategoryProvider provider : rootCategoryProvider.getChildren()){
			List<CategoryPresentation> categoryPresentationList1 = new ArrayList<>();
			for(CategoryService.CategoryProvider provider1 : provider.getChildren()){
				List<CategoryPresentation> categoryPresentationList2 = new ArrayList<>();
				for(CategoryService.CategoryProvider provider2 : provider1.getChildren()) {
					categoryPresentationList2.add(new CategoryPresentation(provider2.getCategory().getName(),
							provider2.getUri(), provider2.getDescription()));
				}
				categoryPresentationList1.add(new CategoryPresentation(provider1.getCategory().getName(),
						provider1.getUri(),provider1.getDescription(),categoryPresentationList2));
			}
			categoryPresentationList.add(new CategoryPresentation(provider.getCategory().getName(),provider.getUri(),provider.getDescription(),categoryPresentationList1));
		}
		rootCategoryPresentation =  new CategoryPresentation(rootCategoryProvider.getCategory().getName(),
				rootCategoryProvider.getUri(),rootCategoryProvider.getDescription(), categoryPresentationList); //
		// делаем объект категории доступным для шаблона по пути "data"
		values.put("data", rootCategoryPresentation);
		// заполняем шаблон данными и получаем результат в виде html с данными
		String html = templateEngine.process("contents.html", new Context(Locale.getDefault(), values));
//		logger.trace("Результат работы шаблонизатора: \n" + html);
		logger.trace("Результат работы шаблонизатора: \n");
		// записываем полученный результат в html файл для просмотра его в браузере.
		//...


		FileOutputStream fileOutputStream = new FileOutputStream("html.html");
		fileOutputStream.write(html.getBytes("UTF-8"));
		fileOutputStream.flush();
		fileOutputStream.close();
//		FileWriter fileWriter = new FileWriter("html.html");
//		fileWriter.write(html);
//		fileWriter.flush();
//		fileWriter.close();
	}
}
