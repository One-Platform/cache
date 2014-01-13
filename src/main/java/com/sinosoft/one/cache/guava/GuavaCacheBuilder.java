package com.sinosoft.one.cache.guava;

import java.util.concurrent.TimeUnit;

import com.sinosoft.one.cache.Cache;
import com.sinosoft.one.cache.CacheBuilder;
import com.sinosoft.one.cache.CacheLoader;
import com.sinosoft.one.cache.LoadingCache;

public final class GuavaCacheBuilder<K,V> implements CacheBuilder<K,V> {
	
	private com.google.common.cache.CacheBuilder<Object, Object> cacheBuilder;
	
	
	GuavaCacheBuilder() {
		this.cacheBuilder=com.google.common.cache.CacheBuilder.newBuilder();
	}
	/**
	 * 默认的CacheBuilder
	 * 
	 * 构造一个默认的CacheBuilder，即：没有任何形式的缓存策略。
	 * @return
	 */
	public static GuavaCacheBuilder<Object, Object> newBuilder(){
		return new GuavaCacheBuilder<Object, Object>();
	}

	public GuavaCacheBuilder<K, V> maximumSize(long size){
		cacheBuilder.maximumSize(size);
		return this;		
	}

	public GuavaCacheBuilder<K, V> expireAfterWrite(long duration, TimeUnit unit){
		cacheBuilder.expireAfterWrite(duration,unit);
		return this;
	}
	
	public GuavaCacheBuilder<K, V> expireAfterAccess(long duration, TimeUnit unit){
		cacheBuilder.expireAfterAccess(duration, unit);
		return this;
	}

	public GuavaCacheBuilder<K, V> refreshAfterWrite(long duration, TimeUnit unit){
		cacheBuilder.refreshAfterWrite(duration, unit);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(
			final CacheLoader<? super K1, V1> loader) {
		com.google.common.cache.CacheLoader<K, V> cacheLoader=new com.google.common.cache.CacheLoader<K, V>(){
			@Override
			public V load(K key) throws Exception {
				return loader.load((K1) key);
			}
		};
		com.google.common.cache.LoadingCache<K1, V1> loadingCache=(com.google.common.cache.LoadingCache<K1, V1>) cacheBuilder.build(cacheLoader);
		return new GuavaLoadingCache<K1, V1>(loadingCache,loadingCache);
	}
	
	public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
		com.google.common.cache.Cache<K1, V1> cache=cacheBuilder.build();
	    return new GuavaCache<K1, V1>(cache);
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
	public <K1 extends K, V1 extends V> GuavaCacheBuilder<K1, V1> removalListener(
	  RemovalListener<? super K1, ? super V1> listener){
		return (GuavaCacheBuilder<K1, V1>) this;
	}*/

}
