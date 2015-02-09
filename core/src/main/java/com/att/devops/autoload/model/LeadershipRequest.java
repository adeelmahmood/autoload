package com.att.devops.autoload.model;

public class LeadershipRequest extends GenericRequest {

	private String path;

	private boolean blockUntilDone;

	public boolean isBlockUntilDone() {
		return blockUntilDone;
	}

	public LeadershipRequest setBlockUntilDone(boolean blockUntilDone) {
		this.blockUntilDone = blockUntilDone;
		return this;
	}

	public String getPath() {
		return path;
	}

	public LeadershipRequest setPath(String path) {
		this.path = path;
		return this;
	}
}
