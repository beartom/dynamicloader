package com.bear.dynamic.loader;

import java.io.IOException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import com.bear.exception.BaseRuntimeException;

/**
 *  Copy and customize from org.mdkt.compiler.ExtendedStandardJavaFileManager
 * @author tanjx
 * @date May 24, 2019
 * @version
 */
public class ExtendedStandardJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	private DynamicClassLoader cl;

	/**
	* Creates a new instance of CSMExtendedStandardJavaFileManager.
	*
	* @param fileManager
	*            delegate to this file manager
	* @param cl
	*/
	protected ExtendedStandardJavaFileManager(JavaFileManager fileManager, DynamicClassLoader cl) {
		super(fileManager);
		this.cl = cl;
	}

	@Override
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className,
			JavaFileObject.Kind kind, FileObject sibling) throws IOException {
		try {
			String originalName = className;
			if (sibling instanceof SourceCode) {
				originalName = ((SourceCode) sibling).getClassName();
			}

			CompiledCode innerClass = new CompiledCode(originalName, className);
			cl.addCode(innerClass);
			return innerClass;
		}
		catch (Exception e) {
			throw new BaseRuntimeException(
					"Error while creating in-memory output file for " + className, e);
		}
	}

}
