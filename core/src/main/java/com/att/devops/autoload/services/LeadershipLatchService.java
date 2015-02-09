package com.att.devops.autoload.services;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeadershipLatchService implements LeadershipService {

	private static final Logger log = LoggerFactory.getLogger(LeadershipLatchService.class);

	private final LeaderLatch latch;

	private LeadershipCallback callback;

	public LeadershipLatchService(CuratorFramework client, String path) {
		latch = new LeaderLatch(client, path);

		// register listener for callbacks
		latch.addListener(new LeaderLatchListener() {
			@Override
			public void notLeader() {
				if (callback != null) {
					callback.ended();
				}
			}

			@Override
			public void isLeader() {
				if (callback != null) {
					callback.obtained();
				}
			}
		});
	}

	@Override
	public void obtainLeadership(LeadershipCallback callback, boolean block) throws IOException {
		obtainLeadership(callback);

		if (block) {
			try {
				latch.await();
			} catch (InterruptedException e) {
				close();
			}
		}
	}

	@Override
	public void obtainLeadership(LeadershipCallback callback) throws IOException {
		this.callback = callback;

		try {
			latch.start();
		} catch (Exception e) {
			log.error("error in starting latch", e);
			close();
		}
	}

	@Override
	public boolean isLeader() {
		return latch.hasLeadership();
	}

	@Override
	public void close() throws IOException {
		latch.close();
		if (callback != null) {
			callback.ended();
		}
	}
}
