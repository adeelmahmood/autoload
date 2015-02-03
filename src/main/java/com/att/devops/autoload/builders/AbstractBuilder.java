package com.att.devops.autoload.builders;

import java.util.concurrent.atomic.AtomicBoolean;

import com.att.devops.autoload.exceptions.BuilderException;

public abstract class AbstractBuilder<O> implements IBuilder<O> {

	private AtomicBoolean building = new AtomicBoolean();

	private O object;	
	
	public final O build() throws BuilderException {
		if (building.compareAndSet(false, true)) {
			object = doBuild();
			return object;
		}
		throw new IllegalStateException("Object already built, build method must be called only once for an object");
	}

	protected abstract O doBuild() throws BuilderException;

	public final O getObject() {
		if (!building.get()) {
			throw new IllegalStateException("Object not built yet, call build method first");
		}
		return object;
	}
}
