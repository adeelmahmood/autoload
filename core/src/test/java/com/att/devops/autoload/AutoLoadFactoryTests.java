package com.att.devops.autoload;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;

import com.att.devops.autoload.exceptions.BuilderException;

public class AutoLoadFactoryTests extends BaseTestClass {

	AutoLoader loader;

	@Test(expected = BuilderException.class)
	public void testEmptyHost() throws BuilderException {
		createLoader(null);
	}

	@Test
	public void testBuilder() throws Exception {
		startServer();
		loader = createLoader(server.getConnectString());

		assertThat(loader, notNullValue());
		assertThat(loader.getClient(), notNullValue());
		assertThat(loader.getClient(), isA(CuratorFramework.class));
		closeServer();
	}

	private AutoLoader createLoader(String host) throws BuilderException {
		return AutoLoadFactory.builder().withHost(host).build();
	}
}
