package com.sinosoft.one.cache;

import java.util.concurrent.TimeUnit;

public interface CacheBuilder<K,V> {
	
	/**
	 * 设置缓存的最大条目数
	 * 
	 * 指定缓存的最大条目数。当缓存的条目数接近这个最大限额时，一些条目会被清除出缓存。
	 * 缓存清除的策略为LRU（最近最少使用）。
	 * 当该值设置为0时，说明缓存立即过期。这在测试时是很有用的，可以暂时禁用缓存而不用修改代码。
	 * @param size
	 *        缓存的最大条目数
	 * @return
	 */
	public CacheBuilder<K, V> maximumSize(long size);

	/**
	 * 写后多长时间过期
	 * 
	 * 指定一条缓存条目被创建（或最近一次更新）后经过多长时间自动被移除缓存。
	 * 当时间设置为0时，说明缓存立即过期。这在测试时是很有用的，可以暂时禁用缓存而不用修改代码。
	 * 过期的缓存条目会在缓存日常维护时进行清理。
	 * @param duration
	 *        时间间隔：缓存条目创建多长时间后被自动删除
	 * @param unit
	 *        时间的单位
	 * @return
	 */
	public CacheBuilder<K, V> expireAfterWrite(long duration, TimeUnit unit);
	
	/**
	 * 读后多长时间过期
	 * 
	 * 指定一条缓存条目被创建（或最近一次更新，或最后一次访问）后经过多长时间自动被移除缓存。
	 * 访问时间可以通过调用缓存的读写操作来重置：Cache.get(Object)，Cache.put(K, V)。
	 * 当时间设置为0时，说明缓存立即过期。这在测试时是很有用的，可以暂时禁用缓存而不用修改代码。
	 * 过期的缓存条目会在缓存日常维护时进行清理。
	 * @param duration
	 *        时间间隔:缓存条目距上次被访问后，经过多长时间被自动删除
	 * @param unit
	 *        时间的单位
	 * @return
	 */
	public CacheBuilder<K, V> expireAfterAccess(long duration, TimeUnit unit);
	
	/**
	 * 写后多长时间刷新
	 * 
	 * 指定缓存的条目被创建以后，每过多长时间自动刷新一次，通过调用CacheLoader.reload()来执行。
	 * 如果构建缓存时不指定这一项，那么可以通过调用Cache.refresh()来手动刷新。
	 * CacheLoader.reload()的默认实现是同步的，这会阻塞其他的缓存操作，所以建议用户使用异步的实现来覆盖CacheLoader.reload()。
	 * 当请求到的是一个旧缓存条目时，刷新操作会自动执行。这会调用CacheLoader.reload()，如果成功，返回新的value，否则返回旧的value。
	 * @param duration
	 *        时间间隔：缓存条目创建后经过多长时间后，value被视为旧value，然后刷新value。
	 * @param unit
	 *        时间单位
	 * @return
	 */
	public CacheBuilder<K, V> refreshAfterWrite(long duration, TimeUnit unit);

	/**
	 * 构建缓存
	 * 
	 * 构建一个缓存，要么使用指定的key返回已经加载过的value，要么使用指定的CacheLoader的原子方法来自动填充缓存。
	 * 如果此时有另一个线程正则加载该key的value，只需等待这个线程完成，然后返回已经加载的value。
	 * 多个线程可以同时加载不同的keys对应的values。
	 * 这个方法不会改变CacheBuilder实例的状态，所以它可以被多次调用，来创建多个独立的缓存。
	 * @param loader
	 *        用于获取新value的缓存加载器
	 * @return {@link Cache}
	 *         满足了所需要的功能的一个缓存
	 */
	public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(
	          CacheLoader<? super K1, V1> loader);
	
	/**
	 * 构建缓存
	 * 
	 * 构建一个缓存，此缓存不通过key来自动加载value。
	 * 这个方法不会改变CacheBuilder实例的状态，所以它可以被多次调用，来创建多个独立的缓存。
	 * @return {@link Cache}
	 *         满足了所需要的功能的一个缓存
	 */
	public <K1 extends K, V1 extends V> Cache<K1, V1> build();

}
