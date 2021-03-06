

package com.bear.dynamic.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bear.dynamic.sample.IHelloWorld;

/**
 * @author tanjx
 * @date May 27, 2019
 * @version
 */
public class DynamicInstanceSample implements IHelloWorld {

	private static final Logger log = LoggerFactory.getLogger(DynamicInstanceSample.class);

	public DynamicInstanceSample() {
		super();
	}

	@Override
	public void print() {
		log.info("Current class type {} ", this.getClass().getName());
		log.info("Hello dynamic world, I am DynamicInstanceSample");
	}

	@Override
	public void dynamicInit() {
		//do something
	}

	@Override
	public void dynamicDestroy() {
		//do something
	}

}
