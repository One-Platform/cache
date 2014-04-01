package com.sinosoft.one.cache;

import java.io.InputStream;
import java.util.Properties;

import com.sinosoft.one.cache.exception.CacheBuilderException;

public class CacheConfig {
    private CacheConfig() {}
	@SuppressWarnings("unused")
	public static final String DEFAULT_FILENAME = "cache.properties";
	private static  Properties properties;

    public static void init(){
        init(DEFAULT_FILENAME);
    }

    /**
     * 给定相对路径创建cacheConfig对象
     * @param fileName
     */
    public static void init(String fileName){
        InputStream in = CacheBuilderSelector.class.getClassLoader()
                .getResourceAsStream(fileName);
        try {
            properties = new Properties();
            properties.load(in);
        } catch (Exception e) {
            throw new CacheBuilderException("加载不到指定的配置文件:" + DEFAULT_FILENAME,
                    e);
        }
    }

	public static Properties getProperties() {
		if (null == properties) {
            init();
		}
		return properties;
	}

    public static Properties getProperties(String fileName) {
        if (null == properties) {
            init(fileName);
        }
        return properties;
    }
}
