package com.bear.dynamic.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.bear.exception.ClassPathUnsolvedException;

/**
 * 动态编译参数设置. 目前仅支持 "-classpath"
 * 
 * @author tanjx
 * @date Aug 8, 2019
 * @version
 */
public class DynamicCompileOptions {

	private static String classpathOption = null;
	
	private DynamicCompileOptions() {
		super();
	}

	public static void setClassPath(String classpath) {
		//do support wild cards for each path. Not my job to verify the path.
		//also add default system class path.
		//once class path is set. do not allow to change.
		if (StringUtils.isBlank(classpathOption)) {
			handleClassPath(classpath);
		}
	}

	public static String[] getOptions() {
		List<String> options = new ArrayList<>();

		if (StringUtils.isNotBlank(classpathOption)) {
			options.add("-classpath");
			options.add(classpathOption);
		}

		//maybe more options in the future. add below

		if (!options.isEmpty()) {
			return options.stream().toArray(String[]::new);
		}
		else {
			return new String[0];
		}
	}

	private static void handleClassPath(String classpath) {
		if (StringUtils.isNotBlank(classpath)) {
			//add default system class path.
			String systemClassPath = System.getProperty("java.class.path");
			StringBuilder realClassPath = new StringBuilder();
			if (StringUtils.isNotBlank(systemClassPath)) {
				realClassPath.append(systemClassPath);
				realClassPath.append(File.pathSeparatorChar);
			}
			//handle class path
			String[] handleFilePathWildcard = handleFilePathWildcard(classpath);
			realClassPath.append(Arrays.asList(handleFilePathWildcard).stream()
					.collect(Collectors.joining(File.pathSeparator)));
			classpathOption = realClassPath.toString();
		}
	}

	/**
	 * handle wild cards in class path.
	 * None archive in sub directory will be included as spec here: {@link} https://docs.oracle.com/javase/8/docs/technotes/tools/windows/classpath.html#A1100762
	 * @param classpath
	 * @return
	 */
	private static String[] handleFilePathWildcard(String classpath) {
		String[] pathes = classpath.split(File.pathSeparator);
		List<String> finalPathes = new ArrayList<>();
		if (ArrayUtils.isNotEmpty(pathes)) {
			for (String path : pathes) {
				if (path.contains("*") && path.lastIndexOf("*") == path.length() - 1) {
					addArchiveInPath(path, finalPathes);
				}
				else {
					finalPathes.add(path);
				}
			}
		}
		return finalPathes.stream().toArray(String[]::new);
	}

	private static void addArchiveInPath(String path, List<String> result) {
		//loop archive jars in this path. Any exception here throw out.
		File file = new File(path);
		if (!file.isDirectory()) {
			throw new ClassPathUnsolvedException(
					"The class path: [" + path + "] with wild cards is not a direcotry.");
		}
		File[] listFiles = file.listFiles();
		for (File subFile : listFiles) {
			if (subFile.isFile() && subFile.getName().endsWith(".jar")) {
					result.add(subFile.getAbsolutePath());
			}
		}
	}
}
