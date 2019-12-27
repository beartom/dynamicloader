package com.bear.dynamic.sample;

import com.bear.dynamic.loader.DynamicInstanceGenerator;
import com.bear.dynamic.loader.IJavaCodeFetcher;

/**
 * @author tanjx
 * @date May 27, 2019
 * @version
 */
public abstract class DynInstanGeneratorFactorySample {

	private static DynamicInstanceGenerator generator = DynamicInstanceGenerator
			.newInstance(new IJavaCodeFetcher() {
				
				@Override
				public byte[] getMD5(String name) {
					//No implement. Will use the default class
					return new byte[0];
				}
				
				@Override
				public String getJavaCode(String name) {
					//No implement. Will use the default class
					return null;
				}
			});

	private DynInstanGeneratorFactorySample() {
		super();
	}

	public static DynamicInstanceGenerator getGenerator() {
		return generator;
	}

	public static DynamicInstanceGenerator setCodeFetcher(IJavaCodeFetcher codeFetcher) {
		generator = DynamicInstanceGenerator.newInstance(codeFetcher);
		return generator;
	}

}
