package com.att.devops.autoload.exceptions;

public class BuilderException extends Exception {

	private static final long serialVersionUID = 1197435857676463858L;

	public BuilderException(String msg) {
		super(msg);
	}
	
	public BuilderException(String msg, Throwable t) {
		super(msg, t);
	}
}
