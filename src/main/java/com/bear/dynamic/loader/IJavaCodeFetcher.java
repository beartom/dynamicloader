package com.bear.dynamic.loader;

/**
 * @author tanjx
 * @date May 24, 2019
 * @version
 */
public interface IJavaCodeFetcher {

	/**
	 * Return the full java source code for this class. if the code is empty, the compiler will skip compare the java code with old one then use the old version class with is compiled by old java code. 
	 * @param name
	 * @return
	 */
	String getJavaCode(String name);

	/**
	 * Return an array if you need to compile all class in this array at the same time.
	 * @return
	 */
	default String[] getAllName(String name) {
		return new String[0];
	}
	
	byte[] getMD5(String name);
}
