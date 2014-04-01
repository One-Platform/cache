package com.sinosoft.one.cache.guava;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.sinosoft.one.cache.LoadingCache;
import com.sinosoft.one.cache.exception.CacheException;

public class GuavaLoadingCache<K, V> extends GuavaCache<K, V> implements
		LoadingCache<K, V> {

	private com.google.common.cache.LoadingCache<K, V> loadingCache;

	public GuavaLoadingCache(com.google.common.cache.Cache<K, V> cache,
			com.google.common.cache.LoadingCache<K, V> loadingCache) {
		super(cache);
		this.loadingCache = loadingCache;
	}

	/**
	 * 获得指定的缓存条目
	 * 
	 * @param key
	 * @return
	 * @throws ExecutionException
	 */
	public V get(K key) throws ExecutionException {
        try {
            return loadingCache.get(key);
        } catch (RuntimeException e){
            throw  new CacheException("com.sinosoft.one.cache.guava.GuavaLoadingCache.get(K key)",e);
        }
	}
	
	/**
	 * 批量获取指定的缓存条目
	 * 
	 * @param keys
	 * @return
	 * @throws ExecutionException
	 */
	public Map<K, V> getAll(Iterable<? extends K> keys)
			throws ExecutionException {
		return loadingCache.getAll(keys);
	}
	
	/**
	 * 重新加载指定key的值，这个操作是异步的。
	 * 加载新value的过程中，如果该key的旧value没有被清除，那么它依然有效。此时如果有请求的话，那么旧的value会被返回。
	 * 如果新value加载成功，那么它会取代旧value。
	 * 加载新value的过程中，如果发生异常，那么该key的旧value会被保留，异常会被吞噬并且记入log。
	 * 如果当前的缓存包括了指定key的键值对，那么会调用CacheLoader.reload()加载新value，否则调用CacheLoader.load()。
	 * 如果进行refresh操作时，发现有另外一个线程正在进行这个操作，那么当前线程什么也不做，直接返回。
	 * @param key
	 */
	public void refresh(K key) {
		loadingCache.refresh(key);
	}

}
