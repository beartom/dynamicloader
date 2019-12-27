package com.bear.dynamic.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanjx
 * @date May 27, 2019
 * @version
 */
public class DynamicWithEmbeddSample implements IHelloWorld {

	private static final Logger log = LoggerFactory.getLogger(DynamicInstanceSample.class);

	private IEmbedded embedd = null;

	public DynamicWithEmbeddSample() {
		super();
		embedd = new EmbeddSample();
	}

	@Override
	public void print() {
		log.info(" This is a Container of Embedded instance. Class : {} ",
				this.getClass().getName());
		log.info(" Hello Container.");
		embedd.print();
	}

}
