package org.ayfaar.app.translation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = {"/spring-basic.xml"})
public class TopicTranslationOldTest implements ApplicationContextAware {
//	@InjectMocks
//	@Autowired
//	TopicTranslation_old topicTranslation;
	//@Autowired
	private ApplicationContext context;


	@Test
	public void testUploadTopics() throws Exception {
//		ApplicationContext context = new ClassPathXmlApplicationContext()
//		topicTranslation.uploadTopics();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
}