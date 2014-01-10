package com.sinosoft.one.cache;

public abstract class CacheLoader<K, V> {

	public abstract V load(K key);
	
	/**
	 * 重新加载指定key的value
	 * 
	 * <p>根据指定的key，重新计算或者检索一个已经被缓存过的键值对。
	 * 当调用CacheBuilder.refreshAfterWrite或者LoadingCache.refresh时，此方法会被调用，用来刷新一个已经存在的缓存条目。
	 * 这个方法的默认实现时线程同步的，当使用CacheBuilder.refreshAfterWrite时，建议用户使用异步的实现来覆写默认实现。
	 * @param key
	 *        用来加载新value
	 * @param oldValue
	 *        key所对应的旧value
	 * @return Future
	 *         新value所关联的future
	 * @throws Exception
	 */
	/*public Future<V> reload(K key, V oldValue){
		Assert.assertNotNull(key);
		Assert.assertNotNull(oldValue);
		return new ImmediateFuture<V>(load(key));
	}*/
}