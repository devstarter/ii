package org.ayfaar.app.configs;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityInit extends AbstractSecurityWebApplicationInitializer {
    public SecurityInit() {
        super(WebSecurityConfig.class);
    }
}
