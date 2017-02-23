package org.ayfaar.app;

import org.springframework.test.context.ActiveProfiles;

/*@SpringBootApplication
@EnableJpaRepositories
@EntityScan("org.ayfaar.app.model")
@ComponentScan("org.ayfaar.app")
@ImportResource({"classpath:hibernate.xml", "classpath:spring-basic.xml"})
@EnableCaching
@EnableAspectJAutoProxy
@Slf4j                */
@ActiveProfiles("dev")
public class SpringTestDevConfiguration extends Application {
}