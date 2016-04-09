package org.ayfaar.app.contents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Configuration
//@Profile(ContentsGeneratorConfig.CONTEXT_GENERATOR_PROFILE)
public class ContentsGeneratorConfig {
	public static final String CONTEXT_GENERATOR_PROFILE = "context-generator";

	@Bean
	public ClassLoaderTemplateResolver defaultTemplateResolver() {
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setPrefix("templates/");
		resolver.setTemplateMode("HTML5");
		resolver.setCharacterEncoding("UTF-8");
		resolver.setOrder(1);
		return resolver;
	}

	@Bean
	@Autowired
	public SpringTemplateEngine springTemplateEngine(TemplateResolver templateResolver) {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver);
		return engine;
	}
}
