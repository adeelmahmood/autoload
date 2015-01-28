package com.att.devops.autoload.builders;

import org.apache.curator.framework.CuratorFramework;

import com.att.devops.autoload.exceptions.BuilderException;
import com.att.devops.autoload.services.LeadershipService;
import com.att.devops.autoload.services.ZKLeadershipService;
import com.google.common.base.Preconditions;

/**
 * LeadershipServiceBuilder
 * 
 * @author adeelq
 *
 */
public class LeadershipServiceBuilder extends ClientBuilderAdapter<CuratorFramework, LeadershipService> {

	private CuratorFramework client;

	private String path;

	public LeadershipServiceBuilder withClient(CuratorFramework client) {
		this.client = client;
		return this;
	}

	public LeadershipServiceBuilder withPath(String path) {
		this.path = path;
		return this;
	}

	@Override
	protected LeadershipService doBuild() throws BuilderException {
		try {
			Preconditions.checkArgument(path != null && !path.isEmpty(), "path must be specified");
			// create leadership service
			LeadershipService service = new ZKLeadershipService(client, path);
			return service;
		} catch (Exception e) {
			throw new BuilderException("error in building leadership service", e);
		}
	}
}
