package com.sinosoft.one.cache;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import com.sinosoft.one.cache.exception.CacheBuilderException;
import com.sinosoft.one.cache.guava.GuavaCacheBuilder;

final class CacheBuilderSelector<K, V> implements CacheBuilder<K, V> {

	private static CacheBuilder<Object, Object> cacheBuilder;
	private final static String CACHE_TYPE = "cache.type";
	
	/**
	 * 通过配置文件选择所使用的缓存，默认为guava cache
	 * 
	 * @param name
	 *        类路径下缓存配置文件的名字
	 * @throws CacheBuilderException 
	 */
	CacheBuilderSelector(String fileName){
		initCacheBuilder();
	}	
	
	@SuppressWarnings("unchecked")
	void initCacheBuilder() {
		String cacheType = (String) CacheConfig.getProperties().get(CACHE_TYPE);
		// 默认是guava cache
		if (null == cacheType || "".equals(cacheType)) {
			cacheBuilder = GuavaCacheBuilder.newBuilder();
		} else {
			try {
				Class<?> builderClass = Class.forName(cacheType);
				Constructor<?> builderConstructor = builderClass
						.getDeclaredConstructor();
				builderConstructor.setAccessible(true);
				Object object = builderConstructor.newInstance();
				if (object instanceof CacheBuilder) {
					cacheBuilder = (CacheBuilder<Object, Object>) object;
				} else {
					throw new CacheBuilderException(CacheConfig.DEFAULT_FILENAME
							+ "的属性cache.type配置错误：" + cacheType);
				}
			} catch (ClassNotFoundException e) {
				throw new CacheBuilderException(CacheConfig.DEFAULT_FILENAME
						+ "的属性cache.type配置错误：" + cacheType, e);
			} catch (SecurityException e) {
				throw new CacheBuilderException("构造方法不允许访问：" + cacheType, e);
			} catch (NoSuchMethodException e) {
				throw new CacheBuilderException("没有默认的构造方法：" + cacheType, e);
			} catch (IllegalArgumentException e) {
				throw new CacheBuilderException(CacheConfig.DEFAULT_FILENAME
						+ "的属性cache.type配置错误：" + cacheType, e);
			} catch (InstantiationException e) {
				throw new CacheBuilderException("类实例化失败：" + cacheType, e);
			} catch (IllegalAccessException e) {
				throw new CacheBuilderException("构造方法不允许访问：" + cacheType, e);
			} catch (InvocationTargetException e) {
				throw new CacheBuilderException("类实例化失败：" + cacheType, e);
			}
		}
	}
	
	/**
	 * 默认的CacheBuilder
	 * 
	 * 构造一个默认的CacheBuilder，即：没有任何形式的缓存策略。
	 * @param name
	 *        类路径下缓存配置文件的名字
	 * @return
	 * @throws CacheBuilderException 
	 */
	public static CacheBuilderSelector<Object, Object> newBuilder(
			String fileName) throws CacheBuilderException {
		return new CacheBuilderSelector<Object, Object>(fileName);
	}

	public CacheBuilderSelector<K, V> maximumSize(long size) {
		cacheBuilder.maximumSize(size);
		return this;
	}

	public CacheBuilderSelector<K, V> expireAfterWrite(long duration,
			TimeUnit unit) {
		cacheBuilder.expireAfterWrite(duration, unit);
		return this;
	}

	public CacheBuilderSelector<K, V> expireAfterAccess(long duration,
			TimeUnit unit) {
		cacheBuilder.expireAfterAccess(duration, unit);
		return this;
	}

	public CacheBuilderSelector<K, V> refreshAfterWrite(long duration,
			TimeUnit unit) {
		cacheBuilder.refreshAfterWrite(duration, unit);
		return this;
	}
	
	
	public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(
			CacheLoader<? super K1, V1> loader) {
		return cacheBuilder.build(loader);
	}

	public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
		return cacheBuilder.build();
	}

	public static CacheBuilderSelector<Object, Object> newBuilder()
			throws CacheBuilderException {
		return new CacheBuilderSelector<Object, Object>(CacheConfig.DEFAULT_FILENAME);
	}
	
	/**
	 * 移除监听器
	 * 
	 * 指定一个监听器实例，所有使用该CacheBuilder的缓存在每次缓存条目被移除时都会被通知。
	 * 使用该CacheBuilder构建的缓存，在缓存条目被移除以后会回调这个方法。
	 * 回调监听器会作为缓存日常维护的一部分。
	 * @param listener
	 *        自定义的监听器
	 * @return
	 */
	/*@SuppressWarnings("unchecked")
	public <K1 extends K, V1 extends V> CacheBuilderSelector<K1, V1> removalListener(
	  RemovalListener<? super K1, ? super V1> listener){
		//cacheBuilder.removalListener(listener);
		return (CacheBuilderSelector<K1, V1>) this;
	}*/
	
}
