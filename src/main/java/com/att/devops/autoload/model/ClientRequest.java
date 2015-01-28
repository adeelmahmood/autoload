package com.att.devops.autoload.model;

public class ClientRequest extends GenericRequest {

	private String host;
	private String ns;

	public ClientRequest(String host, String ns) {
		this.host = host;
		this.ns = ns;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getNs() {
		return ns;
	}

	public void setNs(String ns) {
		this.ns = ns;
	}
}
