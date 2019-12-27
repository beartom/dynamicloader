package com.bear.dynamic.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.tools.SimpleJavaFileObject;

/**
 * Copy and customize from org.mdkt.compiler.CompiledCode
 * @author tanjx
 * @date May 26, 2019
 * @version
 */
public class CompiledCode extends SimpleJavaFileObject {
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private String className;
    private String classNameWithVersion;

    public CompiledCode(String originalClassName,String classNameWithVersion) throws URISyntaxException  {
        super(new URI(originalClassName), Kind.CLASS);
        this.className = originalClassName;
        this.classNameWithVersion = classNameWithVersion;
    }
    
    public String getClassName() {
		return className;
	}
    
    public String getClassNameWithVersion() {
    	return this.classNameWithVersion;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return baos;
    }

    public byte[] getByteCode() {
        return baos.toByteArray();
    }
}
