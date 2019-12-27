package com.bear.dynamic.loader;

import java.util.HashMap;
import java.util.Map;

/**
 * Copy and customize from org.mdkt.compiler.DynamicClassLoader
 * @author tanjx
 * @date May 24, 2019
 * @version
 */
public class DynamicClassLoader extends ClassLoader {

	private Map<String, CompiledCode> customCompiledCode = new HashMap<>();

	/**
	 * Cache the class with real name  
	 */
	private Map<String, Class<?>> cache = new HashMap<>();

	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}

	protected void addCode(CompiledCode cc) {
		customCompiledCode.put(cc.getName(), cc);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		CompiledCode cc = customCompiledCode.get(name);
		if (cc == null) {
			return this.getParent().loadClass(name);
		}
		String realName = cc.getClassNameWithVersion();
		if (cache.containsKey(realName)) {
			return cache.get(realName);
		}
		byte[] byteCode = cc.getByteCode();
		Class<?> defineClass = defineClass(realName, byteCode, 0, byteCode.length);
		cache.put(realName, defineClass);
		return defineClass;
	}

}
