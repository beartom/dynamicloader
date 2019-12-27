package com.bear.dynamic.loader;

/**
 * Interface for dynamic loaded class instance. 
 * 
 * Every object point which is point to dynamic loaded instance must be an interface, must not an implement class.
 * 
 * And the interface of the interface must be a sub interface of IDynamicLoadedInstance.
 * 
 * Support dynamic instance to be embedded dynamic instance. All dynamic instance should be create by DynamicInstanceGenerator. 
 * 
 * For the sample please check DynamicInstanceSample or DynamicWithEmbeddSample
 * 
 * 
 * @author tanjx
 * @date May 23, 2019
 * @version
 */
public interface IDynamicLoadedInstance {

	//Initialize the instance when new instance is created
	default void dynamicInit() {
		//do nothing
	}

	//Destroy the old instance when new instance is created and replace the old one
	default void dynamicDestroy() {
		//do nothing
	}
}
