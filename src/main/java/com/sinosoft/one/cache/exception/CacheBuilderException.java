package com.sinosoft.one.cache.exception;

public class CacheBuilderException extends CacheException {

	private static final long serialVersionUID = -5243663703374659683L;

	public CacheBuilderException() {
		super();
	}

	public CacheBuilderException(String msg) {
		super(msg);
	}

	public CacheBuilderException(String msg, Exception e) {
		super(msg, e);
	}
}
