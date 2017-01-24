package org.ayfaar.app.record;

import org.ayfaar.app.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SetRecordDuration {
	public static void main(String[] args) {
		final ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		RecordHelper recordHelper = context.getBean(RecordHelper.class);
		recordHelper.setRecordsDuration();
	}
}
