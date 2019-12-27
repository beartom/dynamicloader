package com.bear.dynamic.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bear.dynamic.sample.DynInstanGeneratorFactorySample;
import com.bear.dynamic.sample.IEmbedded;
import com.bear.dynamic.sample.IHelloWorld;

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
		embedd = (IEmbedded) DynInstanGeneratorFactorySample.getGenerator().getNewOrDefaultInstance(
				"com.bear.dynamic.sample.EmbeddSample", new EmbeddSample());
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
		log.info(" This is a dynamic2 Container of Embedded instance. Class : {} ",
				this.getClass().getName());
		log.info(" Hello Container.");
		embedd.print();
	}

}
