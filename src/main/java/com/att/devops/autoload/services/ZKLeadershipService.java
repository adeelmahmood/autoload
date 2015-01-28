package com.att.devops.autoload.services;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKLeadershipService extends LeaderSelectorListenerAdapter implements LeadershipService {

	private static final Logger log = LoggerFactory.getLogger(ZKLeadershipService.class);

	private final LeaderSelector selector;

	private LeadershipCallback callback;

	private final CountDownLatch closeLatch = new CountDownLatch(1);

	public ZKLeadershipService(CuratorFramework client, String path) {
		selector = new LeaderSelector(client, path, this);

		// requeue in case disconnected
		selector.autoRequeue();

		// register listeners
		client.getCuratorListenable().addListener(leaderListener);
		client.getUnhandledErrorListenable().addListener(errorsListener);
	}

	@Override
	public void takeLeadership(CuratorFramework client) throws Exception {
		log.debug("leadership obtained, membership participants " + selector.getParticipants());

		// invoke callback
		callback.callback();

		// never give up leadership
		closeLatch.await();
	}

	@Override
	public void obtainLeadership(LeadershipCallback callback) {
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
	}

	CuratorListener leaderListener = new CuratorListener() {
		@Override
		public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
			log.info("Leader listener => Event " + event.getType() + ":" + event.getName() + ":" + event.getPath()
					+ ":" + event.getData());
		}
	};

	UnhandledErrorListener errorsListener = new UnhandledErrorListener() {
		public void unhandledError(String message, Throwable e) {
			log.error("Unrecoverable error: " + message, e);
			try {
				close();
			} catch (IOException ioe) {
				log.warn("Exception when closing.", ioe);
			}
		}
	};
}
