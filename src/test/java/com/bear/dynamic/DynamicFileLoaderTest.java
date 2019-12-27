package com.bear.dynamic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bear.dynamic.loader.DynamicInstanceGenerator;
import com.bear.dynamic.loader.IDynamicLoadedInstance;
import com.bear.dynamic.sample.DynInstanGeneratorFactorySample;
import com.bear.dynamic.sample.DynamicInstanceSample;
import com.bear.dynamic.sample.DynamicWithEmbeddSample;
import com.bear.dynamic.sample.IHelloWorld;
import com.bear.dynamic.sample.JavaCodeFileFetcher;

/**
 * @author tanjx
 * @date May 24, 2019
 * @version
 */
@RunWith(JUnit4.class)
public class DynamicFileLoaderTest {

	private static final Logger log = LoggerFactory.getLogger(DynamicFileLoaderTest.class);

	private static Map<String, String> sourceCodeFileMap = new HashMap<>();
	static {
		sourceCodeFileMap.put("com.bear.dynamic.sample.EmbeddSample",
				"C:\\Users\\tanjx\\Desktop\\EmbeddSample.java");
		sourceCodeFileMap.put("com.bear.dynamic.sample.DynamicInstanceSample",
				"C:\\Users\\tanjx\\Desktop\\DynamicInstanceSample.java");
		sourceCodeFileMap.put("com.bear.dynamic.sample.DynamicWithEmbeddSample",
				"C:\\Users\\tanjx\\Desktop\\DynamicWithEmbeddSample.java");
		sourceCodeFileMap.put("com.bear.dynamic.sample.HelloWorld",
				"C:\\Users\\tanjx\\Desktop\\HelloWorld.java");
	}

	//@Test
	public void testLoad() {

		DynamicInstanceGenerator newInstance = DynamicInstanceGenerator
				.newInstance(new JavaCodeFileFetcher(sourceCodeFileMap));

		IHelloWorld defaultDynamicInstance = new DynamicInstanceSample();
		log.info("---Default instance created with class {}. Print: {}",
				defaultDynamicInstance.getClass().getName());
		defaultDynamicInstance.print();

		try {
			while (true) {
				IHelloWorld newDynamicInstance = (IHelloWorld) newInstance.getNewOrDefaultInstance(
						"com.bear.dynamic.sample.DynamicInstanceSample",
						defaultDynamicInstance);
				newDynamicInstance.print();
				conditionSleep(10, TimeUnit.SECONDS);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//@Test
	public void testEmbedd() {
		DynInstanGeneratorFactorySample.setCodeFetcher(new JavaCodeFileFetcher(sourceCodeFileMap));
		DynamicInstanceGenerator newInstance = DynInstanGeneratorFactorySample.getGenerator();
		IHelloWorld defaultDynamicInstance = new DynamicWithEmbeddSample();
		log.info("---Default instance created with class {}. Print: {}",
				defaultDynamicInstance.getClass().getName());
		defaultDynamicInstance.print();

		try {
			while (true) {
				IHelloWorld newDynamicInstance = (IHelloWorld) newInstance.getNewOrDefaultInstance(
						"com.bear.dynamic.sample.DynamicWithEmbeddSample",
						defaultDynamicInstance);
				newDynamicInstance.print();
				conditionSleep(10, TimeUnit.SECONDS);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateUnexist() {
		DynInstanGeneratorFactorySample.setCodeFetcher(new JavaCodeFileFetcher(sourceCodeFileMap));
		DynamicInstanceGenerator newInstance = DynInstanGeneratorFactorySample.getGenerator();

		try {
			while (true) {

				IDynamicLoadedInstance newOrDefaultInstance = newInstance.getNewOrDefaultInstance(
						"com.bear.dynamic.sample.HelloWorld", null);
				if (newOrDefaultInstance == null) {
					log.error("No instance is created");
				}
				else {
					IHelloWorld hello = (IHelloWorld) newOrDefaultInstance;
					hello.print();
				}
				conditionSleep(10, TimeUnit.SECONDS);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void conditionSleep(long time, TimeUnit timeUnit) {
		Awaitility.setDefaultPollInterval(new Duration(time, timeUnit));
		Date now = new Date();
		Awaitility.await().atMost(time * 2, timeUnit).until(() -> {
			return new Date().getTime() - now.getTime() >= time;
		});
	}
}
