package com.att.devops.autoload.builders;

import com.att.devops.autoload.exceptions.BuilderException;

public interface IBuilder<O> {

	O build() throws BuilderException;
}
