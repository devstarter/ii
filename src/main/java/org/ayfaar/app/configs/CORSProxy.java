package org.ayfaar.app.configs;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author shannah
 */
@WebServlet(
        urlPatterns = {"/cors-proxy"},
        initParams = {
                @WebInitParam(name="targetUri", value="{_target}"),
                @WebInitParam(name="log", value="true"),
                @WebInitParam(name="http.protocol.handle-redirects", value="false")
        }
)
public class CORSProxy extends org.mitre.dsmiley.httpproxy.URITemplateProxyServlet{

    @Override
    public void init() throws ServletException {
        super.init();

    }




    @Override
    protected void copyProxyCookie(HttpServletRequest servletRequest,
                                   HttpServletResponse servletResponse, String headerValue) {
//        List<HttpCookie> cookies = HttpCookie.parse(header.getValue());
//        String path = servletRequest.getContextPath(); // path starts with / or is empty string
//        path += servletRequest.getServletPath(); // servlet path starts with / or is empty string
//
//        for (HttpCookie cookie : cookies) {
//            //set cookie name prefixed w/ a proxy value so it won't collide w/ other cookies
//            String proxyCookieName = getCookieNamePrefix() + cookie.getName();
//            //HttpCookie hc = new HttpCookie()
//            HttpCookie servletCookie = new HttpCookie(proxyCookieName, cookie.getValue());
//            servletCookie.setComment(cookie.getComment());
//            servletCookie.setMaxAge((int) cookie.getMaxAge());
//            if (cookie.getPath() != null) {
//                servletCookie.setPath(cookie.getPath());
//            }
//
//            if (cookie.getDomain() != null) {
//                servletCookie.setDomain(cookie.getDomain());
//            }
//            //servletCookie.setDomain(cookie.getDomain());
//
//            //servletCookie.setPath(path); //set to the path of the proxy servlet
//            // don't set cookie domain
//            //servletCookie.setSecure(cookie.getSecure());
//            servletCookie.setSecure(false);
//            servletCookie.setVersion(cookie.getVersion());
//            //servletResponse.addCookie(servletCookie);
//            servletResponse.addHeader("X-CN1-Set-Cookie", cookie.toString().replaceAll);
//        }
        servletResponse.addHeader("X-CN1-Set-Cookie", headerValue);
    }

    protected String getRealCookie(String cookieValue) {
//        StringBuilder escapedCookie = new StringBuilder();
//        String cookies[] = cookieValue.split("; ");
//        for (String cookie : cookies) {
//            String cookieSplit[] = cookie.split("=");
//            if (cookieSplit.length == 2) {
//                String cookieName = cookieSplit[0];
//                if (cookieName.startsWith(getCookieNamePrefix())) {
//                    cookieName = cookieName.substring(getCookieNamePrefix().length());
//                    if (escapedCookie.length() > 0) {
//                        escapedCookie.append("; ");
//                    }
//                    escapedCookie.append(cookieName).append("=").append(cookieSplit[1]);
//                }
//            }
//
//            cookieValue = escapedCookie.toString();
//        }
        return cookieValue;
    }



    @Override
    protected void copyResponseHeaders(final HttpResponse proxyResponse, final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) {


        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(servletResponse) {

            @Override
            public void addHeader(String name, String value) {
                if ("Location".equalsIgnoreCase(name) || "Set-Cookie".equalsIgnoreCase(name)) {
                    super.addHeader("X-CN1-"+name, value);

                } else if ("Content-Security-Policy".equalsIgnoreCase(name) ) {

                } else {
                    super.addHeader(name, value);
                }
            }




        };
        super.copyResponseHeaders(proxyResponse, servletRequest, responseWrapper); //To change body of generated methods, choose Tools | Templates.
        /*
        servletResponse.setHeader("Access-Control-Expose-Headers", "Set-Cookie, Set-Cookie2");
        Header[] cookies = proxyResponse.getHeaders("Set-Cookie");
        if (cookies != null) {
            for (Header cookie : cookies) {
                servletResponse.addHeader("X-CN1-Set-Cookie", cookie.getValue());
            }
        }
        */
        //servletResponse.setHeader("Access-Control-Allow-Origin", "*");

    }

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse r) throws ServletException, IOException {
        super.service(servletRequest, new HttpServletResponseWrapper(r) {
            @Override
            public void setStatus(int sc) {
                if ((sc < 200 || sc >= 300) && sc != 304) {
                    // XMLHTTPRequest automatically follows redirects.  We don't want that, so we'll
                    // convert 3xx status to non-standard header that we will parse on the other side.
                    super.setIntHeader("X-CN1-Status", sc);
                    super.setStatus(200);
                    return;
                }
                super.setStatus(sc);
            }

            @Override
            public void setStatus(int sc, String sm) {
                if ((sc < 200 || sc >= 300) && sc != 304) {
                    // XMLHTTPRequest automatically follows redirects.  We don't want that, so we'll
                    // convert 3xx status to non-standard header that we will parse on the other side.
                    super.setIntHeader("X-CN1-Status", sc);
                    super.setStatus(200, sm);
                    return;
                }
                super.setStatus(sc, sm);
            }

            @Override
            public void sendError(int sc) throws IOException {
                setStatus(sc, "A server error occurred");
                this.getOutputStream().close();
            }

            @Override
            public void sendRedirect(String location) throws IOException {
                setStatus(302);
                addHeader("X-CN1-Location", location);
                this.getOutputStream().close();

            }



        });



    }





    protected boolean doResponseRedirectOrNotModifiedLogic(
            HttpServletRequest servletRequest, HttpServletResponse servletResponse,
            HttpResponse proxyResponse, int statusCode)
            throws ServletException, IOException {

        // 304 needs special handling.  See:
        // http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
        // We get a 304 whenever passed an 'If-Modified-Since'
        // header and the data on disk has not changed; server
        // responds w/ a 304 saying I'm not going to send the
        // body because the file has not changed.
        if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
            servletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
            servletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return true;
        }
        return false;
    }


    @Override
    protected void copyRequestHeaders(HttpServletRequest servletRequest, HttpRequest proxyRequest) {

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(servletRequest) {

            @Override
            public Enumeration<String> getHeaderNames() {
                Enumeration<String> names = super.getHeaderNames();
                Vector<String> filteredNames = new Vector<String>();
                boolean foundCookie = false;
                boolean foundRealCookie = false;
                while (names.hasMoreElements()) {
                    String name = names.nextElement();
                    Enumeration<String> vals = this.getHeaders(name);

                    if (!name.equalsIgnoreCase("Referer") && !name.equalsIgnoreCase("Host") && !name.equalsIgnoreCase("origin")) {
                        filteredNames.add(name);
                    }
                    if (name.equalsIgnoreCase("X-CN1-Cookie")) {
                        foundCookie = true;
                    }
                    if (name.equalsIgnoreCase("Cookie")) {
                        foundRealCookie = true;
                    }
                }

                if (foundCookie && !foundRealCookie) {
                    filteredNames.add("Cookie");
                }

                return filteredNames.elements();
            }

            @Override
            public Enumeration<String> getHeaders(String name) {

                Enumeration<String> headers = super.getHeaders(name);
                Vector<String> out = new Vector<String>();
                while (headers.hasMoreElements()) {
                    out.add(headers.nextElement());
                }
                if ("Cookie".equalsIgnoreCase(name)) {
                    Enumeration<String> xHeaders = getHeaders("X-CN1-Cookie");
                    while (xHeaders.hasMoreElements()) {
                        out.add(xHeaders.nextElement());
                    }
                }
                return out.elements();
            }




        };
        super.copyRequestHeaders(requestWrapper, proxyRequest);
    }


}
