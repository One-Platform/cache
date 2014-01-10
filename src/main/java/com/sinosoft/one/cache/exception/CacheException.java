package com.sinosoft.one.cache.exception;

public class CacheException extends RuntimeException {

	private static final long serialVersionUID = 7965700454447710870L;

	public CacheException() {
		super();
	}

	public CacheException(String msg) {
		super(msg);
	}

	public CacheException(String msg, Exception e) {
		super(msg, e);
	}
}
