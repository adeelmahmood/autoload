package com.att.devops.autoload;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.att.devops.autoload.builders.AbstractBuilder;
import com.att.devops.autoload.exceptions.BuilderException;
import com.att.devops.autoload.utils.Constants;
import com.google.common.base.Preconditions;

public class AutoLoadFactory {

	public static Builder builder() {
		return new Builder();
	}

	public final static class Builder extends AbstractBuilder<AutoLoader> {

		private String host;

		public Builder withHost(String host) {
			this.host = host;
			return this;
		}

		@Override
		protected AutoLoader doBuild() throws BuilderException {
			AutoLoader autoloader = new AutoLoader();
			try {
				Preconditions.checkArgument(host != null && !host.isEmpty(), "host must be specified");
				// create zk client
				CuratorFramework client = CuratorFrameworkFactory.builder()
											.connectString(host)
											.retryPolicy(new ExponentialBackoffRetry(1000, 3))
											.namespace(Constants.ZK_DEF_NS)
											.build();
				// start the client
				client.start();
				// set the client on auto loader
				autoloader.setClient(client);
			} catch (Exception e) {
				throw new BuilderException("error in building client", e);
			}
			return autoloader;
		}

	}

}
