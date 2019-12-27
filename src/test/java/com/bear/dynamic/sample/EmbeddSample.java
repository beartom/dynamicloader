package com.bear.dynamic.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public void print() {
		log.info(" This is a Embedded instance. Class : {} ", this.getClass().getName());
		log.info(" Hello Embedded.");
	}

}
