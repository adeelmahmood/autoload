package com.att.devops.autoload.builders;

import org.apache.curator.framework.CuratorFramework;

import com.att.devops.autoload.exceptions.BuilderException;
import com.att.devops.autoload.services.LeadershipCallbackService;
import com.att.devops.autoload.services.LeadershipLatchService;
import com.att.devops.autoload.services.LeadershipService;
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

	private ServiceType serviceType = ServiceType.CALLBACK;

	public enum ServiceType {
		LATCH, CALLBACK
	}

	public LeadershipServiceBuilder withClient(CuratorFramework client) {
		this.client = client;
		return this;
	}

	public LeadershipServiceBuilder withPath(String path) {
		this.path = path;
		return this;
	}

	public LeadershipServiceBuilder ofType(ServiceType serviceType) {
		this.serviceType = serviceType;
		return this;
	}

	@Override
	protected LeadershipService doBuild() throws BuilderException {
		try {
			Preconditions.checkArgument(client != null, "service client must be provided");
			Preconditions.checkArgument(path != null && !path.isEmpty(), "path must be specified");
			
			// create leadership service
			LeadershipService service = null;
			if (serviceType == ServiceType.CALLBACK) {
				service = new LeadershipCallbackService(client, path);
			} else if (serviceType == ServiceType.LATCH) {
				service = new LeadershipLatchService(client, path);
			}
			return service;
		} catch (Exception e) {
			throw new BuilderException("error in building leadership service", e);
		}
	}
}
