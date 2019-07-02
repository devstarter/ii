package org.ayfaar.app.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.AppCacheManifestTransformer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment env;

    @Value("${sitemap-dir}")
    private String sitemapDir;


    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

    @Bean OncePerRequestFilter chromeTabSupportFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                Matcher matcher = Pattern
                        .compile("index\\.php\\?option=com_search&searchword=(.*)")
                        .matcher(request.getRequestURI() + "?" + request.getQueryString());
                if (matcher.find()) {
                    response.sendRedirect(matcher.group(1).replace("+", "%20"));
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        boolean devMode = this.env.acceptsProfiles("dev");
        boolean useResourceCache = !devMode;
        Integer cachePeriod = devMode ? 0 : null;

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:static/", "file:"+sitemapDir)
                .setCachePeriod(cachePeriod)
                .resourceChain(useResourceCache)
                .addResolver(new CustomPathResourceResolver())
                .addTransformer(new AppCacheManifestTransformer());
    }

    private class CustomPathResourceResolver extends PathResourceResolver {
        @Override
        public Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
            if (requestPath.startsWith("template/")
                    || requestPath.startsWith(".well-known") // https://ii.ayfaar.org/.well-known/acme-challenge/EqdmKfzENEqmvSQkX1GeW9li7yy-Sf5y6onYtD6Nou4
                    || requestPath.equals("sitemap.xml")) {
                // don't change path
            } else if (requestPath.startsWith("static/")) {
                requestPath = requestPath.replaceFirst("static/", "");
            } else {
                requestPath = "index.html";
            }
            return super.resolveResource(request, requestPath, locations, chain);
        }
    }
}
