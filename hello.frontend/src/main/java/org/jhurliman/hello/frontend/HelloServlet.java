package org.jhurliman.hello.frontend;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import org.jhurliman.hello.backend.IHelloService;

public class HelloServlet extends HttpServlet {
	BundleContext _bc = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			IHelloService helloService = this.<IHelloService>getService("helloService", IHelloService.class);
			if (helloService != null) {
				response.getWriter().println(helloService.getGreeting());
			} else {
				response.getWriter().println("helloService not found");
			}
		} catch (Exception e) {
			response.getWriter().println("Error: " + e.getMessage());
		}
	}

	/*
	 * A generic routine to encapsulate boiler-plate code for dynamic service discovery
	 */
	private <T> T getService(String discoveryId, Class<T> serviceClass) throws InvalidSyntaxException {
		String filter = String.format("(discoverableID=%s)", discoveryId);
		Collection<ServiceReference<T>> serviceRefs = _bc.getServiceReferences(serviceClass, filter);
		if (serviceRefs != null && !serviceRefs.isEmpty()) {
			return _bc.getService(serviceRefs.iterator().next());
		}

		return null;
	}
}
