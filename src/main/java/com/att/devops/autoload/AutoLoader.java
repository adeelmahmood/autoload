package com.att.devops.autoload;

import org.apache.curator.framework.CuratorFramework;

import com.att.devops.autoload.builders.ClientBuilder;
import com.att.devops.autoload.builders.LeadershipServiceBuilder;
import com.att.devops.autoload.exceptions.BuilderException;
import com.att.devops.autoload.model.ClientRequest;
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

	public Clients clients() {
		return new Clients();
	}

	public class Clients {

		public CuratorFramework curatorClient(ClientRequest request) throws BuilderException {
			ClientRequestHandler handler = new ClientRequestHandler(request);
			return handler.getResponse();
		}

		public class ClientRequestHandler extends GenericResponse<CuratorFramework> {
			private final CuratorFramework client;

			public ClientRequestHandler(ClientRequest request) throws BuilderException {
				client = new ClientBuilder().withHost(request.getHost()).withNamespace(request.getNs()).build();
			}

			@Override
			protected CuratorFramework getResponse() {
				return client;
			}
		}
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
				service = new LeadershipServiceBuilder().withClient(req.getClient()).withPath(req.getPath()).build();
			}

			public LeadershipService getResponse() {
				return service;
			}
		}
	}
}
