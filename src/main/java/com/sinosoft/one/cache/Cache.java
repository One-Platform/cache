package com.sinosoft.one.cache;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * 缓存接口，提供对缓存的基本操作。
 * 
 * @version 1.0
 * @date 2013/12/26
 * @author ZFB
 *
 */
public interface Cache<K, V> {

	/**
	 * 从缓存中获取指定key的键值对
	 * 
	 * @param key
	 *        指定的key
	 * @return V
	 *        返回该指定key所关联的value；如果缓存中不存在该key所关联的value，那么返回null
	 */
	V getIfPresent(Object key);
	/**
	 * 存入缓存
	 * 
	 * @param key
	 * @param value
	 */
	void put(K key, V value);
	
	/**
	 * 存入缓存，批量操作
	 * 
	 * 批量存入缓存，效果等同于多次调用put(K key, V value)。
	 * 如果此方法在执行过程中，map内的键值对被修改了，那么此方法的行为就不可预知了。
	 * @param m
	 */
	void putAll(Map<? extends K,? extends V> m);

	/**
	 * 获取指定的缓存条目
	 * 
	 * 
	 * @param key
	 * @param valueLoader
	 * @return
	 * @throws ExecutionException
	 */
	V get(K key, Callable<? extends V> valueLoader) throws ExecutionException;

	/**
	 * 删除指定条目
	 * 
	 * @param key
	 */
	void remove(Object key);

	/**
	 * 批量删除缓存条目
	 * 
	 * @param keys
	 */
	void removeAll(Iterable<?> keys);

	/**
	 * 删除所有的缓存条目
	 */
	void removeAll();

	/**
	 * 正常情况下不建议使用此方法，会影响缓存的性能。
	 * 因为这会重启一个线程来执行清理缓存的操作，这个线程会进行锁操作，这会阻塞其他的线程进行“写缓存”操作。
	 * 因此这个方法适用于“写缓存”操作非常少的场景（如果确实需要调用的话）。
	 */
	void cleanUp();
}
