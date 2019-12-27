package com.bear.exception;

/**
 * Exception for setting class path options for dynamic compiler
 * 
 * @author tanjx
 * @date Aug 8, 2019
 * @version
 */
public class ClassPathUnsolvedException extends BaseRuntimeException{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6860847672637686974L;

	
	public ClassPathUnsolvedException(String message) {
        super(message);
    }

    public ClassPathUnsolvedException(String message, Throwable cause) {
        super(message, cause);
    }
	
	
}
