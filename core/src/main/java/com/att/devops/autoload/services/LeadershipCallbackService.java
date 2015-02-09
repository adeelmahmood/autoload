package com.att.devops.autoload.services;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

public class LeadershipCallbackService extends LeaderSelectorListenerAdapter implements LeadershipService {

	private final LeaderSelector selector;

	private LeadershipCallback callback;

	private final CountDownLatch closeLatch = new CountDownLatch(1);

	public LeadershipCallbackService(CuratorFramework client, String path) {
		selector = new LeaderSelector(client, path, this);

		// requeue in case disconnected
		selector.autoRequeue();
	}

	@Override
	public void takeLeadership(CuratorFramework client) throws Exception {
		// invoke callback
		if (callback != null) {
			callback.obtained();
		}

		// never give up leadership
		closeLatch.await();
	}

	@Override
	public void obtainLeadership(LeadershipCallback callback) throws IOException {
		this.callback = callback;
		// start leadership request process, call is async
		selector.start();
	}

	@Override
	public boolean isLeader() {
		return selector.hasLeadership();
	}

	@Override
	public void close() throws IOException {
		selector.close();
		if (callback != null) {
			callback.ended();
		}
	}

	@Override
	public void obtainLeadership(LeadershipCallback callback, boolean block) throws IOException {
		throw new UnsupportedOperationException(
				"callback service cannot be blocked, use the obtainLeadership(callback) method instead or the latch service");
	}
}
