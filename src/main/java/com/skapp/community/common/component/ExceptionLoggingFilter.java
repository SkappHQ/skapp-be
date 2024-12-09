package com.skapp.community.common.component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ExceptionLoggingFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {
			chain.doFilter(request, response);
		}
		catch (Exception e) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			log.error("Exception intercepted - Type: {}, Message: {}, URI: {}", e.getClass().getSimpleName(),
					e.getMessage(), httpRequest.getRequestURI());
			throw e;
		}
	}

}
