package com.bear.exception;

/**
 * @author tanjx
 * @date May 27, 2019
 * @version
 */
public class DefaultConstructorNotFoundException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7823100531924749372L;

	private final String className;

	public DefaultConstructorNotFoundException(String className) {
		super("Default constructor not found for class : " + className);
		this.className = className;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

}
