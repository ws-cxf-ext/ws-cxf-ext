package org.ws.cxf.ext.correlation;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * HTTP filter to initialize correlation id on client-side.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class CorrelationFilter implements Filter {
	private Object bean;

	private ServletContext context;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
		bean = springContext.getBean("currentCorrelationId");

		context = filterConfig.getServletContext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (null != bean) {
			CurrentCorrelationId currentId = (CurrentCorrelationId) bean;
			currentId.setCurrentCorrelationId(UUID.randomUUID().toString());

			if (null != context) {
				String contextPath = context.getContextPath();

				if (isNotEmpty(contextPath)) {
					String[] tPath = contextPath.split("/");
					if (null != tPath && tPath.length > 0) {
						contextPath = tPath[1];
					}
				}

				currentId.setCurrentClient(contextPath);
			}
		}

		chain.doFilter(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {

	}
}
