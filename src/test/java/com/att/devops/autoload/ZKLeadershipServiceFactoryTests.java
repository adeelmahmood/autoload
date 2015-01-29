package com.att.devops.autoload;

import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.devops.autoload.exceptions.BuilderException;
import com.att.devops.autoload.model.LeadershipRequest;
import com.att.devops.autoload.services.LeadershipCallback;
import com.att.devops.autoload.services.LeadershipService;

public class ZKLeadershipServiceFactoryTests {

	AutoLoader loader;
	TestingServer server;

	@Before
	public void init() throws Exception {
		server = new TestingServer();
		loader = AutoLoadFactory.builder()
						.withHost(server.getConnectString())
						.build();
	}

	@Test
	public void test() throws BuilderException {
		LeadershipRequest request = new LeadershipRequest();
		request.setPath("/m");

		LeadershipService service = loader.services().leadership(request);
		service.obtainLeadership(new LeadershipCallback() {
			@Override
			public void callback() {
				System.out.println("im leader");
			}
		});

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@After
	public void end() {
		CloseableUtils.closeQuietly(server);
	}
}
