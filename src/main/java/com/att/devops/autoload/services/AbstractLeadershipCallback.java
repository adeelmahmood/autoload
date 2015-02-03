package com.att.devops.autoload.services;

/**
 * AbstractLeadershipCallback provides noop methods for
 * {@link LeadershipCallback} interface. This allows the subclasses to only
 * implement the methods they are interested in
 * 
 * @author adeelq
 *
 */
public abstract class AbstractLeadershipCallback implements LeadershipCallback {

	@Override
	public void obtained() {
	}

	@Override
	public void ended() {
	}
}
