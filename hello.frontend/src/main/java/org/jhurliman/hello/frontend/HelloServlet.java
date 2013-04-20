package org.jhurliman.hello.frontend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class HelloServlet extends HttpServlet {
	BundleContext _bc = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//IHelloService helloService = this.<IHelloService>getService("helloService", IHelloService.class.getName());
			//if (helloService != null) {
			//	response.getWriter().println(helloService.getGreeting());
			//} else {
				response.getWriter().println("helloService not found");
			//}
		} catch (Exception e) {
			response.getWriter().println("Error: " + e.getMessage());
		}
	}

	/*
	 * A generic routine to encapsulate boiler-plate code for dynamic service discovery
	 */
	private <T> T getService(String discoveryId, String type) throws InvalidSyntaxException {
		T service = null;

		String filter = String.format("(discoverableID=%s)", discoveryId);
		ServiceReference[] sr = _bc.getServiceReferences(type, filter);
		if (sr != null && sr.length > 0) {
			service = (T)_bc.getService(sr[0]);
		}

		return service;
	}
}
