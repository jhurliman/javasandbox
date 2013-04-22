package org.jhurliman.hello.backend;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service("HelloService")
public class HelloService implements IHelloService {
	protected final Log log = LogFactory.getLog(this.getClass());

	public String getGreeting() {
		return "Hello World";
	}
}
