package com.att.devops.autoload.builders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

import com.att.devops.autoload.AutoLoadFactory;
import com.att.devops.autoload.AutoLoader;
import com.att.devops.autoload.BaseTestClass;
import com.att.devops.autoload.builders.LeadershipServiceBuilder.ServiceType;
import com.att.devops.autoload.exceptions.BuilderException;
import com.att.devops.autoload.services.LeadershipCallbackService;
import com.att.devops.autoload.services.LeadershipLatchService;
import com.att.devops.autoload.services.LeadershipService;

public class LeadershipServiceBuilderTests extends BaseTestClass {

	LeadershipService service;

	@Test(expected = BuilderException.class)
	public void testEmptyPath() throws BuilderException {
		new LeadershipServiceBuilder().withPath(null).build();
	}

	@Test(expected = BuilderException.class)
	public void testNoClient() throws BuilderException {
		new LeadershipServiceBuilder().withPath("abc").withClient(null).build();
	}

	@Test
	public void testBuilder() throws BuilderException {
		startServer();
		AutoLoader loader = AutoLoadFactory.builder().withHost(server.getConnectString()).build();

		// create service using the client on loader
		// we are intentionally not using loader to create the service
		// as these tests are directly for builder objects
		service = new LeadershipServiceBuilder().withPath("/a/b/c").withClient(loader.getClient()).build();

		assertThat(service, notNullValue());
		assertThat(service.isLeader(), is(false));
		assertThat(service, is(instanceOf(LeadershipCallbackService.class)));

		LeadershipService service2 = new LeadershipServiceBuilder().withPath("/a/b/c").withClient(loader.getClient())
				.ofType(ServiceType.LATCH).build();

		assertThat(service2, notNullValue());
		assertThat(service2, is(instanceOf(LeadershipLatchService.class)));
		closeServer();
	}

	@Test(expected = IllegalStateException.class)
	public void testBuilderRepeatedBuilds() throws BuilderException {
		startServer();
		AutoLoader loader = AutoLoadFactory.builder().withHost(server.getConnectString()).build();

		LeadershipServiceBuilder builder = new LeadershipServiceBuilder();
		builder.withPath("/a").withClient(loader.getClient()).build();
		builder.withPath("/b").withClient(loader.getClient()).build();

		closeServer();
	}

}
