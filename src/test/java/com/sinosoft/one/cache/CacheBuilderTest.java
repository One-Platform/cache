package com.sinosoft.one.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CacheBuilderTest {

	private static final Logger logger=Logger.getLogger(CacheBuilderTest.class.getName());
	private LoadingCache< String, String> loaderCache;
	@SuppressWarnings("unused")
	private Cache< String, String> callableCache;

	@Before
	public void initCache() {
		//故意用来测试github merge的
		//针对整个cache定义一个原子操作，根据给定的key获取value，需要重写原子操作load(V key)
		//使用默认的同步方式的reload(K key, V oldValue)方法
		loaderCache=CacheBuilderSelector.newBuilder()
				.maximumSize(3)//缓存最大条目数为3
				.expireAfterAccess(3, TimeUnit.SECONDS)//访问间隔大于3秒，缓存条目自动过期
				.expireAfterWrite(10, TimeUnit.SECONDS)//写后10秒，缓存条目自动过期
				.refreshAfterWrite(6, TimeUnit.SECONDS)//写后6秒，自动刷新缓存条目
				.build(new CacheLoader<String, String> (){
					@Override
					public String load(String key) {
						long value=System.currentTimeMillis();
						logger.info("from load(),value:::"+value);
						return String.valueOf(value);
					}					
				});
		
		//构造一个Cache，不需要指定原子操作，在使用时再指定
		//由于没有默认的load()和reload()方法，所以这种方式不能使用refreshAfterWrite()
		callableCache=CacheBuilderSelector.newBuilder()
				.maximumSize(3)//缓存最大条目数为3
				.expireAfterAccess(5, TimeUnit.SECONDS)//访问间隔大于5秒，缓存条目自动过期
				.expireAfterWrite(8, TimeUnit.SECONDS)//写后8秒，缓存条目自动过期
				.build();
	}
	//测试最大条目数
	//允许的最大条目是3，当接近最大条目数时，就开始清除缓存
	@Test
	public void testMaximumSize() throws ExecutionException{
		loaderCache.put("a", "1");
		loaderCache.put("b", "2");
		loaderCache.put("c", "3");
		loaderCache.put("d", "4");
		loaderCache.put("f", "5");
		Assert.assertNotSame("1", loaderCache.get("a"));
	}
	//测试访问间隔超过后，缓存自动过期
	@SuppressWarnings("static-access")
	@Test
	public void testExpireAfterAccess() throws ExecutionException{
		String value1=loaderCache.get("one");
		String value2=loaderCache.get("one");
		//访问时间间隔内，缓存有效
		Assert.assertEquals(value1, value2);
		try {
			Thread.currentThread().sleep(4*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//访问时间间隔超过，缓存条目过期
		String value3=loaderCache.get("one");
		Assert.assertNotSame(value2, value3);
	}
	//测试写后多长时间，缓存条目自动过期
	@SuppressWarnings("static-access")
	@Test
	public void testExpireAfterWrite() throws ExecutionException{
		LoadingCache<String, String> expireWriteCache=CacheBuilderSelector.newBuilder("cache.properties")
				.expireAfterWrite(2, TimeUnit.SECONDS)
				.build(new CacheLoader<String, String>(){
					@Override
					public String load(String key) {
						return "abc";
					}
					
				});
		expireWriteCache.put("two", "2");
		String value1=expireWriteCache.get("two");
		Assert.assertEquals("2", value1);
		try {
			Thread.currentThread().sleep(3*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String value2=expireWriteCache.get("two");
		Assert.assertNotSame(value1, value2);
	}
	//测试写后多长时间，缓存条目自动刷新
	@SuppressWarnings("static-access")
	@Test
	public void testRefreshAfterWrite() throws ExecutionException{
		LoadingCache<String, String> refreshWriteCache=CacheBuilderSelector.newBuilder("cache.properties")
				.expireAfterAccess(10, TimeUnit.SECONDS)
				.refreshAfterWrite(2, TimeUnit.SECONDS)//写后2秒，自动刷新
				.expireAfterWrite(20, TimeUnit.SECONDS)
				.build(new CacheLoader<String, String>(){
					@Override
					public String load(String key) {
						return String.valueOf(System.currentTimeMillis());
					}
					
				});
		//refreshWriteCache.put("three", "3");
		String value1=refreshWriteCache.get("three");
		//刷新前的值相等
		Assert.assertEquals(value1, refreshWriteCache.get("three"));
		try {
			Thread.currentThread().sleep(3*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String value2=refreshWriteCache.get("three");
		//自动刷新后，获取新的value
		Assert.assertNotSame(value1, value2);
	}
	//Callable Cache的过期策略同CacheLoader，但是没有自动刷新，省略测试用例
}
