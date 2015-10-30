package org.ayfaar.app.spring.converter.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;

@Component("jacksonObjectMapper")
public class CustomObjectMapper extends ObjectMapper {

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        SerializationConfig serialConfig = getSerializationConfig()
                .withDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        serialConfig.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        this.setSerializationConfig(serialConfig);
    }
}
