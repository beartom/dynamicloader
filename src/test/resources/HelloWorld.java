package com.bear.dynamic.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bear.dynamic.sample.IHelloWorld;

/**
 * @author tanjx
 * @date May 27, 2019
 * @version
 */
public class HelloWorld implements IHelloWorld {

	private static final Logger log = LoggerFactory.getLogger(IHelloWorld.class);

	public HelloWorld() {
		super();
	}

	@Override
	public void print() {
		log.info("Current class type {} ", this.getClass().getName());
		log.info("Hello dynamic world");
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
