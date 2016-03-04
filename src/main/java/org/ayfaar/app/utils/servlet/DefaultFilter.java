package org.ayfaar.app.utils.servlet;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(urlPatterns = "/css/*")
@Component
public class DefaultFilter implements Filter {

    private RequestDispatcher defaultRequestDispatcher;
    @Inject
    ResourceLoader resourceLoader;

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
//        defaultRequestDispatcher.forward(request, response);
//        resourceLoader.getResource(request.)
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.defaultRequestDispatcher =
                filterConfig.getServletContext().getNamedDispatcher("default");
    }
}
