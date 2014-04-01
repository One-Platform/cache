package com.sinosoft.one.cache.guava;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.sinosoft.one.cache.Cache;
import com.sinosoft.one.cache.exception.CacheException;

public class GuavaCache<K, V> implements Cache<K, V> {

	private com.google.common.cache.Cache<K, V> cache;

	public GuavaCache(com.google.common.cache.Cache<K, V> cache) {
		this.cache = cache;
	}

	public V getIfPresent(Object key) {
		return cache.getIfPresent(key);
	}

	public void put(K key, V value) {
		cache.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		cache.putAll(m);
	}

	public V get(K key, Callable<? extends V> valueLoader)
			throws ExecutionException {
        try {
            return cache.get(key, valueLoader);
        } catch (RuntimeException e){
            throw  new CacheException("com.sinosoft.one.cache.guava.GuavaCache.get(K key, Callable<? extends V> valueLoader)",e);
        }
	}

	public void remove(Object key) {
		cache.invalidate(key);
	}

	public void removeAll(Iterable<?> keys) {
		cache.invalidateAll(keys);
	}

	public void removeAll() {
		cache.invalidateAll();
	}

	public void cleanUp() {
		cache.cleanUp();
	}

}
