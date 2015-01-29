package com.att.devops.autoload.services;

import java.io.Closeable;
import java.io.IOException;

public interface LeadershipService extends Closeable {

	void obtainLeadership(LeadershipCallback callback, boolean block) throws IOException;

	void obtainLeadership(LeadershipCallback callback) throws IOException;
	
	boolean isLeader();

}
