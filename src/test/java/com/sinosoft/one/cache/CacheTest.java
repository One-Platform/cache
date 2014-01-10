package com.sinosoft.one.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class CacheTest {
	
	private LoadingCache< String, String> loaderCache;
	private Cache< String, String> callableCache;
	@Before
	public void initCache() {
		//针对整个cache定义一个原子操作，根据给定的key获取value，需要重写原子操作load(V key)
		//使用默认的同步方式的reload(K key, V oldValue)方法
		loaderCache=CacheBuilderSelector.newBuilder()
				.expireAfterWrite(100, TimeUnit.SECONDS)
				.build(new CacheLoader<String, String> (){
					@Override
					public String load(String key) {
						return "abc";
					}
					
				});
		//构造一个Cache，不需要指定原子操作，在使用时再指定
		callableCache=CacheBuilderSelector.newBuilder("cache.properties")
				.expireAfterWrite(100, TimeUnit.SECONDS)
				.build();
	}
	//测试put
	@Test
	public void testPut() throws ExecutionException{
		loaderCache.put("one", "value1");
		Assert.assertEquals("value1", loaderCache.get("one"));
	}
	//测试getIfPresent,没有自填充操作
	@Test
	public void testGetIfPresent(){
		String notExist=loaderCache.getIfPresent("y");
		Assert.assertEquals(null, notExist);
		loaderCache.put("y", "xyz");
		String hasExist=loaderCache.getIfPresent("y");
		Assert.assertEquals("xyz", hasExist);
	}
	
	//测试putAll
	@Test
	public void testPutAll() throws ExecutionException{
		Map<String, String> map=new HashMap<String, String>();
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		loaderCache.putAll(map);
		Assert.assertEquals("2", loaderCache.get("b"));
	}
	//使用Callable构建Cache，在使用时再指定原子操作
	@Test
	public void testGet() throws ExecutionException{
		String value=callableCache.get("a", new Callable<String>() {
			public String call() throws Exception {
				return "abc";
			}
		});
		Assert.assertEquals("abc", value);
	}
	
	//测试remove
	@Test
	public void testRemove() throws ExecutionException{
		loaderCache.put("a", "123");
		Assert.assertEquals("123", loaderCache.get("a"));
		loaderCache.remove("a");
		Assert.assertNotSame("123", loaderCache.get("a"));
	}
	//测试removeAll，指定keys
	@Test
	public void testRemoveAllWithKeys() throws ExecutionException{
		Map<String, String> map=new HashMap<String, String>();
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		loaderCache.putAll(map);
		Assert.assertEquals("1", loaderCache.get("a"));
		Assert.assertEquals("2", loaderCache.get("b"));
		Assert.assertEquals("3", loaderCache.get("c"));
		List<String> list=new ArrayList<String>();
		list.add("a");
		list.add("b");
		Iterable<String> keys=list;
		loaderCache.removeAll(keys);
		Assert.assertNotSame("1", loaderCache.get("a"));
		Assert.assertNotSame("2", loaderCache.get("b"));
		Assert.assertEquals("3", loaderCache.get("c"));
	}
	//测试removeAll，所有
	@Test
	public void testRemoveAll() throws ExecutionException{
		Map<String, String> map=new HashMap<String, String>();
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		loaderCache.putAll(map);
		Assert.assertEquals("1", loaderCache.get("a"));
		Assert.assertEquals("2", loaderCache.get("b"));
		Assert.assertEquals("3", loaderCache.get("c"));
		loaderCache.removeAll();
		Assert.assertNotSame("1", loaderCache.get("a"));
		Assert.assertNotSame("2", loaderCache.get("b"));
		Assert.assertNotSame("3", loaderCache.get("c"));
	}
	//测试cleanup
	@Test
	public void testCleanUp() throws ExecutionException{
		Map<String, String> map=new HashMap<String, String>();
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		loaderCache.putAll(map);
		Assert.assertEquals("1", loaderCache.get("a"));
		Assert.assertEquals("2", loaderCache.get("b"));
		Assert.assertEquals("3", loaderCache.get("c"));
		loaderCache.removeAll();
		Assert.assertNotSame("1", loaderCache.get("a"));
		Assert.assertNotSame("2", loaderCache.get("b"));
		Assert.assertNotSame("3", loaderCache.get("c"));
	}
	
	@Test
	public void testCallable() throws ExecutionException{
		Cache< String, String> cache=CacheBuilderSelector.newBuilder()
				.expireAfterWrite(10, TimeUnit.SECONDS)
				.build();
		String callableValue=cache.get("1",new Callable<String>() {
			public String call() throws Exception {
				return "callableValue";
			}
		});
		Assert.assertEquals("callableValue", callableValue);
	}
}
