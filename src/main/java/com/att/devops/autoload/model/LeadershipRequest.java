package com.att.devops.autoload.model;

import org.apache.curator.framework.CuratorFramework;

public class LeadershipRequest extends GenericRequest {

	private CuratorFramework client;

	private String path;

	public CuratorFramework getClient() {
		return client;
	}

	public LeadershipRequest setClient(CuratorFramework client) {
		this.client = client;
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
