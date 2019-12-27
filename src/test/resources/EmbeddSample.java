package com.bear.dynamic.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bear.dynamic.sample.IEmbedded;

/**
 * @author tanjx
 * @date May 27, 2019
 * @version
 */
public class EmbeddSample implements IEmbedded {

	private static final Logger log = LoggerFactory.getLogger(DynamicInstanceSample.class);

	public EmbeddSample() {
		super();
	}


	@Override
	public void dynamicInit() {
		//Do something
	}

	@Override
	public void dynamicDestroy() {
		//do something
	}

	@Override
	public void print() {
		log.info(" This is a dynamic4 Embedded instance. Class : {} ", this.getClass().getName());
		log.info(" Hello Embedded.");
	}

}
