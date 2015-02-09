package com.att.devops.autoload;

import org.apache.curator.framework.CuratorFramework;

import com.att.devops.autoload.builders.LeadershipServiceBuilder;
import com.att.devops.autoload.builders.LeadershipServiceBuilder.ServiceType;
import com.att.devops.autoload.exceptions.BuilderException;
import com.att.devops.autoload.model.GenericResponse;
import com.att.devops.autoload.model.LeadershipRequest;
import com.att.devops.autoload.services.LeadershipService;

/**
 * AutoLoader
 * 
 * @author adeelq
 *
 */
public class AutoLoader {

	private CuratorFramework client;

	public CuratorFramework getClient() {
		return client;
	}

	public void setClient(CuratorFramework client) {
		this.client = client;
	}

	public Services services() {
		return new Services();
	}

	public class Services {

		public LeadershipService leadership(LeadershipRequest request) throws BuilderException {
			LeadershipRequestHandler handler = new LeadershipRequestHandler(request);
			return handler.getResponse();
		}

		public class LeadershipRequestHandler extends GenericResponse<LeadershipService> {
			private final LeadershipService service;

			public LeadershipRequestHandler(LeadershipRequest req) throws BuilderException {
				service = new LeadershipServiceBuilder()
								.withClient(client)
								.withPath(req.getPath())
								.ofType(req.isBlockUntilDone() ? ServiceType.LATCH : ServiceType.CALLBACK)
								.build();
			}

			public LeadershipService getResponse() {
				return service;
			}
		}
	}
}
