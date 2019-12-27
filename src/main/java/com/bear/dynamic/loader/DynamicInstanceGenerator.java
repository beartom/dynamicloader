package com.bear.dynamic.loader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bear.exception.DefaultConstructorNotFoundException;

/**
 * 
 * Use this generator very carefully. 
 * 
 * An util to dynamically to load java source code from codeFether then compile to a new class. 
 * Return  a new instance of  the new class if source code changed, otherwise return the old instance.
 * 
 * 
 * @author tanjx
 * @date May 27, 2019
 * @version
 */
public class DynamicInstanceGenerator {

	private static final Logger log = LoggerFactory.getLogger(DynamicInstanceGenerator.class);

	private InMemoryJavaCompiler compiler;

	private IJavaCodeFetcher codeFether;

	private Map<String, Class<?>> cache = new HashMap<>();

	private DynamicInstanceGenerator(IJavaCodeFetcher codeFether) {
		super();
		this.compiler = InMemoryJavaCompiler.newInstance(codeFether);
		this.codeFether = codeFether;
	}

	public static DynamicInstanceGenerator newInstance(IJavaCodeFetcher codeFether) {
		return new DynamicInstanceGenerator(codeFether);
	}

	/**
	 * Return new instance of new class if the compiler found the new java source code and complier all success.
	 * If any error happen when compiler or newInstance. Return the currentInstance. So you may get null if you pass null as currentInstance.
	 * 
	 * 
	 * @param className
	 * @param currentInstance
	 * @return
	 * @throws ClassNotFoundException
	 */
	public IDynamicLoadedInstance getNewOrDefaultInstance(String className,
			IDynamicLoadedInstance currentInstance) {

		Class<?> targetClass = null;
		try {
			targetClass = complieAndGetTargetClass(className);
		}
		catch (ClassNotFoundException e) {
			log.error("Serious error happened. Class {} not found in system class loader",
					className, e);
			return currentInstance;
		}

		if (targetClass == null) {
			//should never comes here.
			return currentInstance;
		}

		//found class
		if (currentInstance != null && currentInstance.getClass() == targetClass) {
			//return exactly the same instance as input parameter.
			return currentInstance;
		}
		else {
			try {
				IDynamicLoadedInstance newDynamicInstance = createNewInstance(targetClass);
				if (newDynamicInstance != null) {
					newDynamicInstance.dynamicInit();
				}
				if (currentInstance != null) {
					currentInstance.dynamicDestroy();
				}
				return newDynamicInstance;
			}
			catch (Exception e) {
				log.error(
						"Serious error happened. Error to create instance for dynamic class : {}.",
						targetClass.getClass().getName(), e);
				return currentInstance;
			}
		}
	}

	/**
	 * Try compile all source code. Get compiled class if all success, otherwise get from cache, otherwise get from system class loader.
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException Throw ClassNotFoundException when the class even not found in system class loader.
	 */
	private Class<?> complieAndGetTargetClass(String className) throws ClassNotFoundException {
		String[] allDynamicClassName = codeFether.getAllName(className);
		//if no source code. Don't compile 
		if (!(allDynamicClassName == null || allDynamicClassName.length == 0)) {

			Class<?> targetClass = null;
			try {
				Map<String, Class<?>> compiledResult = compiler.compile(allDynamicClassName);
				targetClass = compiledResult.get(className);
				cache.putAll(compiledResult);
				return targetClass;
			}
			catch (Exception e) {
				log.error(
						"Serious error happened. The memory compiler fail when complile for : [{}]",
						allDynamicClassName, e);
			}
		}
		//use cache
		if (cache.containsKey(className)) {
			return cache.get(className);
		}
		else {
			//use default one from system class loader.
			return this.getClass().getClassLoader().loadClass(className);
		}
	}

	private IDynamicLoadedInstance createNewInstance(Class<?> clazz) throws InstantiationException,
			IllegalAccessException, InvocationTargetException, DefaultConstructorNotFoundException {
		//be careful when destroy currentInstance
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();

		Constructor<?> defaultConstructor = null;

		for (Constructor<?> constructor : constructors) {

			if (constructor.getParameterCount() == 0) {
				defaultConstructor = constructor;
				if (Modifier.isPrivate(constructor.getModifiers())) {
					constructor.setAccessible(true);
				}
			}
		}
		if (defaultConstructor != null) {
			Object newInstance = defaultConstructor.newInstance();
			if (newInstance != null) {
				return (IDynamicLoadedInstance) newInstance;
			}
		}
		else {
			throw new DefaultConstructorNotFoundException(clazz.getName());
		}
		//should never comes here.
		return null;
	}

}
