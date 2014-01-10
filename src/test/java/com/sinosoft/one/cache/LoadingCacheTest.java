package com.sinosoft.one.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class LoadingCacheTest {
	
	private static final Logger logger=Logger.getLogger(LoadingCacheTest.class.getName());
	private LoadingCache< String, String> loaderCache;
	@Before
	public void initCache() {
		//针对整个cache定义一个原子操作，根据给定的key获取value，需要重写原子操作load(V key)
		//使用默认的同步方式的reload(K key, V oldValue)方法
		loaderCache=CacheBuilderSelector.newBuilder("cache.properties")
				.expireAfterWrite(100, TimeUnit.SECONDS)
				.build(new CacheLoader<String, String> (){
					@Override
					public String load(String key) {
						return "abc";
					}
				});
	}
	
	//测试get方法，使用CacheLoader指定原子操作，自填充
	@Test
	public void testGetWithLoad() throws ExecutionException{
		String value=loaderCache.get("a");
		Assert.assertEquals("abc", value);
	}
	//测试getAll，指定keys
	@Test
	public void testGetAllWithKeys() throws ExecutionException{
		Map<String, String> map=new HashMap<String, String>();
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		loaderCache.putAll(map);
		List<String> list=new ArrayList<String>();
		list.add("a");
		list.add("b");
		Iterable<String> keys=list;
		Map<String, String> result=loaderCache.getAll(keys);
		Assert.assertEquals("1", result.get("a"));
		Assert.assertEquals("2", result.get("b"));
		Assert.assertEquals(null, result.get("c"));
		Assert.assertEquals(2, result.size());
	}
	//测试refresh，使用异步方式
	@SuppressWarnings("static-access")
	@Test
	public void testRefresh() throws ExecutionException, InterruptedException{
		final LoadingCache< String, String> cache=CacheBuilderSelector.newBuilder("cache.properties")
				.expireAfterWrite(10000, TimeUnit.SECONDS)
				.refreshAfterWrite(4, TimeUnit.SECONDS)
				.build(new CacheLoader<String, String> (){
					@Override
					public String load(String key) {
						long value=System.currentTimeMillis();
						logger.info("from load(),value:::"+value);
						return String.valueOf(value);
					}					
				});
		//初始时，缓存内不包括key=a的键值对，此时cache.refresh()会调用CacheLoader.load()
		cache.refresh("a");
		Runnable runnable=new Runnable() {
			public void run() {
				logger.info(Thread.currentThread().getName()+":::start");
				cache.refresh("a");
				try {
					logger.info(Thread.currentThread().getName()+":::end,value:::"+cache.get("a"));
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		};
		//开启多个线程同时刷新缓存数据，只有一个得到刷新后的value，其余的线程得到旧的value
		for(int i=0;i<5;i++){
			Thread thread=new Thread(runnable, "thread"+i);
			thread.start();
		}
		Thread.currentThread().sleep(1*1000);
		logger.info("after refreshed");
		logger.info("value:::"+cache.get("a"));
		Thread.currentThread().sleep(5*1000);
		logger.info("end");
	}

}
