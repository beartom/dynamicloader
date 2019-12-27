package com.bear.dynamic.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bear.dynamic.loader.IJavaCodeFetcher;
import com.google.common.base.Strings;

/**
 * @author tanjx
 * @date May 24, 2019
 * @version
 */
public class JavaCodeFileFetcher implements IJavaCodeFetcher {

	private static final Logger log = LoggerFactory.getLogger(JavaCodeFileFetcher.class);

	private Map<String, String> sourceCodeFileMap;

	private Map<String, byte[]> md5Map = new HashMap<>();

	/**
	 * 
	 */
	public JavaCodeFileFetcher(Map<String, String> sourceCodeFileMap) {
		super();
		this.sourceCodeFileMap = sourceCodeFileMap;
	}

	@Override
	public String getJavaCode(String name) {
		String path = sourceCodeFileMap.get(name);
		String codeContent = null;
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));) {

			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			codeContent = sb.toString();

		}
		catch (FileNotFoundException e) {
			log.error("Java source file not found for load class : {}.", name, e);
		}
		catch (IOException e) {
			log.error("Fail to read java source file for load class: {}.", name, e);
		}
		
		if (!Strings.isNullOrEmpty(codeContent)) {
			try {

				MessageDigest md5Deigest = MessageDigest.getInstance("MD5");
				md5Map.put(name, md5Deigest.digest(codeContent.getBytes()));

			}
			catch (NoSuchAlgorithmException e) {
				log.error("No Algorithm MD5 found in java.security.MessageDigest", e);
			}
		}

		return codeContent;
	}

	@Override
	public String[] getAllName(String name) {
		return sourceCodeFileMap.keySet().stream().toArray(String[]::new);
	}

	@Override
	public byte[] getMD5(String name) {
		if (md5Map.containsKey(name)) {
			return md5Map.get(name);
		}
		else {
			return new byte[0];
		}
	}
}
