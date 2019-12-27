package com.bear.dynamic.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bear.exception.CompilationException;
import com.google.common.base.Strings;

/**
 *  Copy and customize from org.mdkt.compiler.InMemoryJavaCompiler
 * @author tanjx
 * @date May 24, 2019
 * @version
 */
public class InMemoryJavaCompiler {

	private static final Logger log = LoggerFactory.getLogger(InMemoryJavaCompiler.class);

	private JavaCompiler javac;
	private DynamicClassLoader classLoader;
	private Iterable<String> options;
	private boolean ignoreWarnings = false;
	private IJavaCodeFetcher codeFetcher;
	
	private ExtendedStandardJavaFileManager fileManager;

	private Map<String, SourceCode> sourceCodes = new HashMap<>();

	public static InMemoryJavaCompiler newInstance(IJavaCodeFetcher codeFetcher) {
		return new InMemoryJavaCompiler(codeFetcher);
	}

	private InMemoryJavaCompiler(IJavaCodeFetcher codeFetcher) {
		this.javac = ToolProvider.getSystemJavaCompiler();
		this.classLoader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
		//DynamicCompileOptions will set the options
		this.options = Arrays.asList(DynamicCompileOptions.getOptions());
		this.codeFetcher = codeFetcher;
		this.fileManager = new ExtendedStandardJavaFileManager(
				javac.getStandardFileManager(null, null, null), classLoader);
	}

	public InMemoryJavaCompiler useParentClassLoader(ClassLoader parent) {
		this.classLoader = new DynamicClassLoader(parent);
		return this;
	}

	/**
	 * Options used by the compiler, e.g. '-Xlint:unchecked'.
	 *
	 * @param options
	 * @return
	 */
	public InMemoryJavaCompiler useOptions(String... options) {
		//give a chance to change options
		this.options = Arrays.asList(options);
		return this;
	}

	/**
	 * Ignore non-critical compiler output, like unchecked/unsafe operation
	 * warnings.
	 *
	 * @return
	 */
	public InMemoryJavaCompiler ignoreWarnings() {
		ignoreWarnings = true;
		return this;
	}

	/**
	 * Compile single source
	 *
	 * @param className
	 * @param sourceCode
	 * @return
	 * @throws Exception
	 */
	public Class<?> compile(String className) throws ClassNotFoundException {
		return compile(new String[] { className }).get(className);
	}

	/**
	 * Compile single source
	 *
	 * @param className
	 * @param sourceCode
	 * @return
	 * @throws Exception
	 */
	public Map<String, Class<?>> compile(String[] classNameArr) throws ClassNotFoundException {
		Map<String, Class<?>> result = new HashMap<>();
		//find replaceable class
		Map<String, SourceCode> replaceSourceCodes = new HashMap<>();
		//save existing class name and the real class name with version
		Map<String, String> existingClassRealName = new HashMap<>();
		//save empty source code class name
		List<String> emptySourceCodeClassName = new ArrayList<>();

		distinguishClass(classNameArr, replaceSourceCodes, existingClassRealName,
				emptySourceCodeClassName);

		//Find existing class in DynamicClassLoader
		for (Map.Entry<String, String> entry : existingClassRealName.entrySet()) {
			result.put(entry.getKey(), this.classLoader.loadClass(entry.getValue()));
		}

		// if not reload and not exist. find from system class loader. If not exist then try DynamicClassLoader again.
		for (String className : emptySourceCodeClassName) {
			Class<?> clazz = null;
			try {
				clazz = this.classLoader.loadClass(className);
			}
			catch (ClassNotFoundException e) {
				//ignore
			}

			if (clazz == null) {
				if (this.sourceCodes.containsKey(className)) {
					clazz = this.classLoader
							.loadClass(this.sourceCodes.get(className).getRealClassName());
				}
				else {
					throw new ClassNotFoundException(
							String.format("Class not found which name is %s", className));
				}
			}

			result.put(className, clazz);
		}

		if (replaceSourceCodes.size() > 0) {
			log.info("Recompile following class: {}",replaceSourceCodes.keySet());
			Map<String, Class<?>> recompiledClass = compileAll(replaceSourceCodes);
			result.putAll(recompiledClass);
			sourceCodes.putAll(replaceSourceCodes);
		}
		return result;
	}

	private void distinguishClass(String[] classNameArr, Map<String, SourceCode> replaceSourceCodes,
			Map<String, String> existingClassRealName, List<String> emptySourceCodeClassName) {
		for (String className : classNameArr) {
			String sourceCode = codeFetcher.getJavaCode(className);
			byte[] md5 = codeFetcher.getMD5(className);
			//save class which source code is empty.
			if (Strings.isNullOrEmpty(sourceCode)) {
				emptySourceCodeClassName.add(className);
				continue;
			}

			if (sourceCodes.containsKey(className)) {
				SourceCode csmSourceCode = sourceCodes.get(className);
				String realClassName = csmSourceCode.getRealClassName();
				boolean replaced = csmSourceCode.compareAndReplace(md5, sourceCode);
				if (replaced) {
					replaceSourceCodes.put(className, csmSourceCode);
				}
				else {
					existingClassRealName.put(className, realClassName);
				}
			}
			else {
				SourceCode csmSourceCode = new SourceCode(className,md5,sourceCode);
				replaceSourceCodes.put(className, csmSourceCode);
			}
		}
	}

	/**
	 * Compile all sources
	 *
	 * @return Map containing instances of all compiled classes
	 * @throws Exception
	 */
	private Map<String, Class<?>> compileAll(Map<String, SourceCode> replaceSourceCodes)
			throws ClassNotFoundException {
		if (replaceSourceCodes.size() == 0) {
			throw new CompilationException("No source code to compile");
		}
		log.info("Start to complie with options: {}", this.options);
		Collection<SourceCode> compilationUnits = replaceSourceCodes.values();
		DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
		JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, collector, options,
				null, compilationUnits);
		boolean result = task.call();
		if (!result || collector.getDiagnostics().isEmpty()) {
			StringBuilder exceptionMsg = new StringBuilder();
			exceptionMsg.append("Unable to compile the source");
			boolean hasWarnings = false;
			boolean hasErrors = false;
			for (Diagnostic<? extends JavaFileObject> d : collector.getDiagnostics()) {
				switch (d.getKind()) {
				case NOTE:
				case MANDATORY_WARNING:
				case WARNING:
					hasWarnings = true;
					break;
				case OTHER:
				case ERROR:
				default:
					hasErrors = true;
					break;
				}
				exceptionMsg.append("\n").append("[kind=").append(d.getKind());
				exceptionMsg.append(", ").append("line=").append(d.getLineNumber());
				exceptionMsg.append(", ").append("message=").append(d.getMessage(Locale.US))
						.append("]");
			}
			if (hasWarnings && !ignoreWarnings || hasErrors) {
				throw new CompilationException(exceptionMsg.toString());
			}
		}

		Map<String, Class<?>> classes = new HashMap<>();
		for (String className : replaceSourceCodes.keySet()) {
			classes.put(className, classLoader.findClass(className));
		}
		return classes;
	}
}
