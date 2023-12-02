package account.logging;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
@WebFilter(urlPatterns = "/*")
public class RequestLoggingFilter implements Filter {

    private final Logger logger;

    public RequestLoggingFilter() throws IOException {
        this.logger = MyLogger.setupLogger();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;

            CacheRequestWrapper wrappedRequest = new CacheRequestWrapper(httpServletRequest);

            logger.info("Request Method: " + wrappedRequest.getMethod() + "\n" +
                    "Request URI: " + wrappedRequest.getRequestURI() + "\n" +
                    "Request Body: " + wrappedRequest.getBody() + "\n" +
                    "Authorization Header: " + wrappedRequest.getHeader("Authorization") + "\n");

            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    public static class CacheRequestWrapper extends HttpServletRequestWrapper {

        private final String body;

        public CacheRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        }
        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    // Not implemented
                }

                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
        }

        public String getBody() {
            return this.body;
        }
    }

}
