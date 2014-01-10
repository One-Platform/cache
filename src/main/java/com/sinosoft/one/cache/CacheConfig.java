package com.sinosoft.one.cache;

import java.io.InputStream;
import java.util.Properties;

import com.sinosoft.one.cache.exception.CacheBuilderException;

public class CacheConfig {

	@SuppressWarnings("unused")
	private static CacheConfig instance;
	public static final String DEFAULT_FILENAME = "cache.properties";
	private static final Properties properties;
	static {
		InputStream in = CacheBuilderSelector.class.getClassLoader()
				.getResourceAsStream(DEFAULT_FILENAME);
		properties = new Properties();
		try {
			properties.load(in);
		} catch (Exception e) {
			throw new CacheBuilderException("加载不到指定的配置文件:" + DEFAULT_FILENAME,
					e);
		}
	}

	private CacheConfig() {

	}

	public static Properties getProperties() {
		if (null == properties) {
			instance = new CacheConfig();
		}
		return properties;
	}
}
