package org.ayfaar.app.contents;

import org.apache.commons.io.FileUtils;
import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.utils.CategoryService;
import org.ayfaar.app.utils.contents.CategoryPresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
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
		CategoryService.CategoryProvider rootCategoryProvider = categoryService.getByName("Том 4");
		logger.trace("Загруженная категория: " + rootCategoryProvider.extractCategoryName());
		// в этом объект нужно положить всю нужную в шаблоне информацию о категрии (имя, описание, дочерние категории...)
		CategoryPresentation rootCategoryPresentation = null; //
		List<CategoryPresentation> categoryPresentationList = fillCategoryPresentationList(rootCategoryProvider.getChildren());;
		rootCategoryPresentation =  new CategoryPresentation(rootCategoryProvider.getParentUri().substring(rootCategoryProvider.getParentUri().indexOf(":")+1)+"."+rootCategoryProvider.extractCategoryName(),
				rootCategoryProvider.getUri(),rootCategoryProvider.getDescription(), categoryPresentationList); //
		// делаем объект категории доступным для шаблона по пути "data"
		values.put("data", rootCategoryPresentation);
		// заполняем шаблон данными и получаем результат в виде html с данными
		String html = templateEngine.process("contents.html", new Context(Locale.getDefault(), values));
//		logger.trace("Результат работы шаблонизатора: \n" + html);
		logger.trace("Генерация шаблона окончена.");
		// записываем полученный результат в html файл для просмотра его в браузере.
		// вариант записи в файл - какой больше понравится

//		FileOutputStream fileOutputStream = new FileOutputStream("html.html");
//		fileOutputStream.write(html.getBytes("UTF-8"));
//		fileOutputStream.flush();
//		fileOutputStream.close();

		FileUtils.writeStringToFile(new File("html.html"), html, Charset.forName("UTF-8"));
	}

	public static List<CategoryPresentation> fillCategoryPresentationList(List<CategoryService.CategoryProvider> rootCategoryProvider){
		// рекурсивно заполняем список cPList
		List<CategoryPresentation> cPList = new ArrayList<>();
		for (CategoryService.CategoryProvider provider : rootCategoryProvider){
			String name = provider.extractCategoryName();
			String[] nameArr = name.split("\\.");
			if(nameArr.length > 2)
				name = nameArr[nameArr.length-2]+"."+nameArr[nameArr.length-1];
			// рекурсивно заполняем список, со всеми вложенными детьми
			cPList.add(new CategoryPresentation(name, provider.getStartItemNumber(),
					provider.getDescription(), new ArrayList<>(fillCategoryPresentationList(provider.getChildren()))));
		}
		return cPList;
	}
}
