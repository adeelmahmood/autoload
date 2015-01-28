package com.att.devops.autoload.builders;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.att.devops.autoload.exceptions.BuilderException;
import com.att.devops.autoload.utils.Constants;
import com.google.common.base.Preconditions;

/**
 * ClientBuilder
 * 
 * @author adeelq
 *
 */
public class ClientBuilder extends AbstractBuilder<CuratorFramework> {
	
	private String host;
	private String ns = Constants.ZK_DEF_NS;

	public ClientBuilder withHost(String host) {
		this.host = host;
		return this;
	}

	public ClientBuilder withNamespace(String ns) {
		this.ns = ns;
		return this;
	}
	
	@Override
	protected CuratorFramework doBuild() throws BuilderException {
		try {
			Preconditions.checkArgument(host != null && !host.isEmpty(), "host must be specified");
			// create zk client
			CuratorFramework client = CuratorFrameworkFactory.builder()
										.connectString(host)
										.retryPolicy(new ExponentialBackoffRetry(1000, 3))
										.namespace(ns)
										.build();
			// start the client
			client.start();
			return client;
		} catch (Exception e) {
			throw new BuilderException("error in building client", e);
		}
	}

}
