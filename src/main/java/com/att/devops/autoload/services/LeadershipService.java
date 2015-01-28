package com.att.devops.autoload.services;

import java.io.Closeable;

public interface LeadershipService extends Closeable {

	void obtainLeadership(LeadershipCallback callback);

	boolean isLeader();
}
