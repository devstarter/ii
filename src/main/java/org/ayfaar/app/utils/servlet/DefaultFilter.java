package org.ayfaar.app.utils.servlet;

import javax.servlet.*;
import java.io.IOException;

public class DefaultFilter implements Filter {

    private RequestDispatcher defaultRequestDispatcher;

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        defaultRequestDispatcher.forward(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.defaultRequestDispatcher =
                filterConfig.getServletContext().getNamedDispatcher("default");
    }
}
