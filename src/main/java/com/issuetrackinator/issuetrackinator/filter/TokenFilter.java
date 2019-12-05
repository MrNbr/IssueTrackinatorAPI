package com.issuetrackinator.issuetrackinator.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.issuetrackinator.issuetrackinator.repository.UserRepository;

@Component
@Order(1)
public class TokenFilter implements Filter
{

    private final static String TOKEN_HEADER_NAME = "api_key";

    @Autowired
    UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Enumeration<String> headerNames = httpRequest.getHeaderNames();

        String requestMethod = httpRequest.getMethod();
        String requestURI = httpRequest.getRequestURI();

        if (requestMethod.equals("POST") && requestURI.contains("/api/users")
            || requestMethod.equals("GET") && requestURI.contains("/api/issues")
            || requestMethod.equals("GET") && requestURI.contains("/api/users/regenerateToken"))
        {
            chain.doFilter(request, response);
        }
        else
        {
            boolean headerFound = false;
            while (headerNames.hasMoreElements())
            {
                String name = headerNames.nextElement();
                if (name.equals(TOKEN_HEADER_NAME))
                {
                    headerFound = true;
                    if (userRepository.findByToken(httpRequest.getHeader(name)).isPresent())
                    {
                        chain.doFilter(request, response);
                    }
                    else
                    {
                        ((HttpServletResponse) response).sendError(404,
                            "There's no user with this token");
                        return;
                    }

                }
            }
            if (!headerFound)
            {
                ((HttpServletResponse) response).sendError(400, "There's no token in the header");
            }

        }
        // TODO Auto-generated method stub
    }

}
