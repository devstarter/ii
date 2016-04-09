package org.ayfaar.app;

import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.ServletContext;

/*@SpringBootApplication
@EnableJpaRepositories
@EntityScan("org.ayfaar.app.model")
@ComponentScan("org.ayfaar.app")
@ImportResource({"classpath:hibernate.xml", "classpath:spring-basic.xml"})
@EnableCaching
@EnableAspectJAutoProxy
@Slf4j
@ActiveProfiles("dev")*/
public class SpringTestConfiguration {
    @Bean
    public ServletContext servletContext() {
        return new MockServletContext();
    }
}