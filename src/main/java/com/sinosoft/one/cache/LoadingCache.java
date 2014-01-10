package com.sinosoft.one.cache;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface LoadingCache<K, V> extends Cache<K, V> {
	
	/**
	 * 获得指定的缓存条目
	 * 
	 * @param key
	 * @return
	 * @throws ExecutionException
	 */
	V get(K key) throws ExecutionException;
	
	/**
	 * 批量获取指定的缓存条目
	 * 
	 * @param keys
	 * @return
	 * @throws ExecutionException
	 */
	Map<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException;
	
	/**
	 * 重新加载指定key的值。
	 * 加载新value的过程中，如果该key的旧value没有被清除，那么它依然有效。此时如果有请求的话，那么旧的value会被返回。
	 * 如果新value加载成功，那么它会取代旧value。
	 * 加载新value的过程中，如果发生异常，那么该key的旧value会被保留，异常会被吞噬并且记入log。
	 * 如果当前的缓存包括了指定key的键值对，那么会调用CacheLoader.reload()加载新value，否则调用CacheLoader.load()。
	 * 如果进行refresh操作时，发现有另外一个线程正在进行这个操作，那么当前线程什么也不做，直接返回。
	 * <p>注意：如果没有覆写CacheLoader.reload()，那么这个操作是同步的；建议以异步方式覆写CacheLoader.reload()。
	 * @param key
	 */
	void refresh(K key);

}
