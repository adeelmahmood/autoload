package com.att.devops.autoload;

import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

public class BaseTestClass {

	protected TestingServer server;

	public void startServer() {
		try {
			server = new TestingServer();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void closeServer() {
		CloseableUtils.closeQuietly(server);
	}	
}
