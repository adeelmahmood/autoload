package com.att.devops.autoload.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.devops.autoload.AutoLoadFactory;
import com.att.devops.autoload.AutoLoader;
import com.att.devops.autoload.BaseTestClass;
import com.att.devops.autoload.exceptions.BuilderException;
import com.att.devops.autoload.model.LeadershipRequest;
import com.att.devops.autoload.services.AbstractLeadershipCallback;
import com.att.devops.autoload.services.LeadershipCallback;
import com.att.devops.autoload.services.LeadershipService;

public class LeadershipCallbackApiServiceTests extends BaseTestClass {

	AutoLoader loader;

	@Before
	public void init() throws BuilderException {
		startServer();
		loader = AutoLoadFactory.builder().withHost(server.getConnectString()).build();
	}

	@After
	public void close() {
		closeServer();
	}

	@Test
	public void testOneLeader() throws BuilderException, IOException, InterruptedException {
		LeadershipRequest request = new LeadershipRequest().setPath("/m");
		final LeadershipService service = loader.services().leadership(request);
		final CountDownLatch latch = new CountDownLatch(1);

		assertThat(service, notNullValue());
		assertThat(service.isLeader(), is(false));

		System.out.println("requesting leadership ...");
		try {
			// request leadership
			service.obtainLeadership(new AbstractLeadershipCallback() {
				@Override
				public void obtained() {
					System.out.println("leadership obtained");
					assertThat(service.isLeader(), is(true));
					try {
						service.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					latch.countDown();
				}
			});
		} catch (Exception e) {
			latch.countDown();
		}

		// wait until test completed 
		latch.await();
	}

	@Test
	public void testMultipleLeaders() throws Exception {
		int n = 3;
		final LeadershipRequest request = new LeadershipRequest().setPath("/m");
		final CountDownLatch latch = new CountDownLatch(n);
		for (int i = 1; i <= n; i++) {
			LeadershipService service = loader.services().leadership(request);
			service.obtainLeadership(new TestLeadershipCallback(service, latch));
		}
		System.out.println("now waiting ... " + Thread.currentThread().getName());
		latch.await();
		assertEquals(latch.getCount(), 0);
	}
	
//	@Test
	public void testMultipleLeadersWithLostServerConnection() throws Exception {
		int n = 3;
		final LeadershipRequest request = new LeadershipRequest().setPath("/m");
		final CountDownLatch latch = new CountDownLatch(n);
		for (int i = 1; i <= n; i++) {
			LeadershipService service = loader.services().leadership(request);
			service.obtainLeadership(new AbstractLeadershipCallback() {
				@Override
				public void obtained() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
					System.out.println("about to restart server ...");
					closeServer();
				}
				@Override
				public void ended() {
					System.out.println("lost leadership ...");
				}
			});
		}
		System.out.println("now waiting ... " + Thread.currentThread().getName());
		latch.await();
		assertEquals(latch.getCount(), 0);
	}

	/*
	 * Test Listener
	 */
	public static class TestLeadershipCallback implements LeadershipCallback {
		private final AtomicInteger count = new AtomicInteger();
		private final CountDownLatch latch;
		private final LeadershipService service;

		public TestLeadershipCallback(LeadershipService service, CountDownLatch latch) {
			this.service = service;
			this.latch = latch;
		}

		@Override
		public void obtained() {
			System.out.println("I have been leader " + count.incrementAndGet() + " times - "
					+ Thread.currentThread().getName());
			try {
				Thread.sleep(500);
				service.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void ended() {
			System.out.println("Not a leader anymore - " + Thread.currentThread().getName());
			latch.countDown();
		}
	}
}
