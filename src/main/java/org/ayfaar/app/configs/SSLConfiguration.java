package org.ayfaar.app.configs;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "server.ssl.enabled")
public class SSLConfiguration {
    @Bean
    public EmbeddedServletContainerFactory containerCustomizer(@Value("${server.port}") Integer httpsPort,
                                                               @Value("${server.http-port}") Integer httpPort,
                                                               @Value("${server.ssl.redirect-http-to-https:false}") Boolean redirectHttpToHttps) {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                if (redirectHttpToHttps) {
                    SecurityConstraint securityConstraint = new SecurityConstraint();
                    securityConstraint.setUserConstraint("CONFIDENTIAL");
                    SecurityCollection collection = new SecurityCollection();
                    collection.addPattern("/*");
                    securityConstraint.addCollection(collection);
                    context.addConstraint(securityConstraint);
                } else {
                    super.postProcessContext(context);
                }
            }
        };

        // initiate HTTP connection with conditional redirect
        Connector connector = new Connector(TomcatEmbeddedServletContainerFactory.DEFAULT_PROTOCOL);
        connector.setPort(httpPort);
        if (redirectHttpToHttps) connector.setRedirectPort(httpsPort);

        tomcat.addAdditionalTomcatConnectors();

        tomcat.addConnectorCustomizers(connector1 -> connector1.setAsyncTimeout(60000));

        return tomcat;
    }
}
