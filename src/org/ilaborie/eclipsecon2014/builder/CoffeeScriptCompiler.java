package org.ilaborie.eclipsecon2014.builder;

import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.core.resources.IFile;

/**
 * The CoffeeScript compiler (Using Nashorn).
 * @see http://blog.javabien.net/2014/02/19/use-nashorn-and-webjars-to-execute-javascript-server-side/
 */
public class CoffeeScriptCompiler {

	/** The Nashorn ScriptEngine */
	private static final ScriptEngine NASHORN = (new ScriptEngineManager())
			.getEngineByName("nashorn");

	/** List of coffeescript extension */
	private static final List<String> COFFEE_SCRIPT_EXTENSSION = Arrays.asList(
			"coffee", "cs", "coffeescript");

	/**
	 * Compile coffee script.
	 *
	 * @param source
	 *            the source
	 * @return the compiled file or null
	 * @throws ScriptException
	 */
	public static String compileCoffeeScript(IFile source)
			throws ScriptException {
		if (!isCoffeeScript(source))
			return null;
		// FIXME Compile
		Object result = NASHORN.eval(String.format(
				"print('Shoud compile %s ');", source));
		return (result != null) ? result.toString() : null;
	}

	/**
	 * Checks if file is coffee script.
	 *
	 * @param file
	 *            the file
	 * @return true, if it's coffee script
	 */
	private static boolean isCoffeeScript(IFile file) {
		return COFFEE_SCRIPT_EXTENSSION.contains(file.getFileExtension());
	}

}
