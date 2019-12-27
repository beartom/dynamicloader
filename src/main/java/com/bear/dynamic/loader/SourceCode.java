package com.bear.dynamic.loader;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.SimpleJavaFileObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bear.exception.BaseRuntimeException;

/**
 *  Copy and customize from org.mdkt.compiler.SourceCode
 * @author tanjx
 * @date May 24, 2019
 * @version
 */
public class SourceCode extends SimpleJavaFileObject {

	private static final Logger log = LoggerFactory.getLogger(SourceCode.class);

	private long lastModified = 0;

	private String replaceableContents = null;

	private byte[] lastMD5ForContents = null;

	private AtomicInteger version = new AtomicInteger(0);

	private static final Pattern CLASSNAME_PATTERN = Pattern
			.compile("class\\s+[A-Z]([a-z]|[A-Z]|[0-9])*\\s?");

	private static final String BRACKETS = "\\(\\)";

	private String className;

	/**
	 * @param className
	 * @param contents
	 * @throws Exception
	 */
	public SourceCode(String className,byte[] newMD5, String contents) {
		super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension),
				Kind.SOURCE);
		this.className = className;
		this.replaceableContents = contents;
		this.lastMD5ForContents=newMD5;
	}

	@Override
	public long getLastModified() {
		return this.lastModified;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public boolean isNameCompatible(String simpleName, Kind kind) {
		String baseName = simpleName + kind.extension;
		String baseNameWithNoVersion = baseName.replace("V" + version.get() + ".java", ".java");
		return kind.equals(getKind())
				&& (baseName.equals(toUri().getPath()) || toUri().getPath().endsWith("/" + baseName)
						|| toUri().getPath().endsWith("/" + baseNameWithNoVersion));
	}

	/* (non-Javadoc)
	 * @see org.mdkt.compiler.SourceCode#getCharContent(boolean)
	 */
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return replaceContentWithVersion();
	}

	protected String getRealClassName() {
		String realClassName = this.getClassName() + "V" + version.get();
		log.info("The real classname is [{}]", realClassName);
		return realClassName;
	}

	protected boolean compareAndReplace(byte[] newMD5, String contents) {
		this.lastModified = new Date().getTime();
		if (Arrays.equals(newMD5, this.lastMD5ForContents)) {
			return false;
		}
		else {
			log.info("The java file contents which classname is {} has changed",
					this.getClassName());
			this.replaceableContents = contents;
			this.lastMD5ForContents = newMD5;
			this.version.incrementAndGet();
			return true;
		}
	}

	private String replaceContentWithVersion() {
		Matcher matcher = CLASSNAME_PATTERN.matcher(this.replaceableContents);

		if (matcher.find()) {
			String classNameStr = matcher.group(0);

			String classShortName = getClassShorName();
			String afterReplace = this.replaceableContents
					.replaceFirst(classNameStr,
							String.format("class %sV%d ", classShortName, this.version.get()))
					.replaceAll("public " + classShortName + BRACKETS,
							String.format("public %sV%d%s", classShortName, this.version.get(),
									BRACKETS))
					.replaceAll("private " + classShortName + BRACKETS,
							String.format("private %sV%d%s", classShortName, this.version.get(),
									BRACKETS))
					.replaceAll("protected " + classShortName + BRACKETS, String.format(
							"protected %sV%d%s", classShortName, this.version.get(), BRACKETS));
			if (log.isDebugEnabled()) {
				log.debug("The real content after replace is : [{}]", afterReplace);
			}
			return afterReplace;
		}
		throw new BaseRuntimeException(
				"Unable to match class name in code for class:" + getClassName());
	}

	private String getClassShorName() {
		return this.className.substring(this.className.lastIndexOf('.') + 1);
	}

}
