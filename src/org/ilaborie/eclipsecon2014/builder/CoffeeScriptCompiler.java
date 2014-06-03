package org.ilaborie.eclipsecon2014.builder;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The CoffeeScript compiler (Using Nashorn).
 * 
 * @see http://blog.javabien.net/2014/02/19/use-nashorn-and-webjars-to-execute-
 *      javascript-server-side/
 */
public class CoffeeScriptCompiler {

	/** List of coffeescript extension */
	private static final List<String> COFFEE_SCRIPT_EXTENSSION = Arrays.asList(
			"coffee", "cs", "coffeescript");

	/** CoffeeScript Compiler */
	private static final String COMPILER = "/coffee-script-1.7.1.min.js";

	/** The Constant BUFFER_SIZE. */
	private static final int BUFFER_SIZE = 4 * 1024;

	/** The Constant EOF. */
	private static final int EOF = -1;

	/** The Nashorn ScriptEngine */
	private final ScriptEngine nashorn;

	/** The compiled script. */
	private CompiledScript compiledScript;

	/** The bindings. */
	private Bindings bindings;

	/**
	 * Instantiates a new coffee script compiler.
	 */
	public CoffeeScriptCompiler() {
		super();
		this.nashorn = (new ScriptEngineManager()).getEngineByName("nashorn");

		try {
			InputStream in = this.getClass().getResourceAsStream(COMPILER);
			String script = readScript(in, StandardCharsets.UTF_8.name());
			compiledScript = ((Compilable) nashorn).compile(script
					+ "\nCoffeeScript.compile(__source, {bare: true});");
			bindings = nashorn.getBindings(ScriptContext.ENGINE_SCOPE);
		} catch (ScriptException e) {
			throw new RuntimeException("Unable to compile script", e);
		}
	}

	/**
	 * Read script.
	 *
	 * @param in
	 *            the input stream (will be closed)
	 * @return the script
	 */
	private String readScript(InputStream in, String charset) {
		try (InputStream input = in) {
			byte[] buffer = new byte[BUFFER_SIZE];
			try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
				int count;
				while (EOF != (count = input.read(buffer))) {
					bytes.write(buffer, 0, count);
				}
				return bytes.toString(charset);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to read stream !", e);
		}
	}

	/**
	 * Compile coffee script.
	 *
	 * @param source
	 *            the source
	 * @param monitor
	 * @return the compiled file or null
	 * @throws ScriptException
	 * @throws CoreException
	 */
	public Optional<String> compileCoffeeScript(IFile source,
			IProgressMonitor monitor) throws ScriptException, CoreException {
		if (!isCoffeeScript(source))
			return empty();
		// Load content
		monitor.subTask("Reading file: " + source.getCharset());
		String coffee = readScript(source.getContents(), source.getCharset());
		// Bind
		bindings.put("__source", coffee);
		// Compile (execute compile script)
		monitor.subTask("Compiling file: " + source.getCharset());
		Object result = compiledScript.eval(bindings).toString();
		return (result != null) ? of(result.toString()) : empty();
	}

	/**
	 * Checks if file is coffee script.
	 *
	 * @param file
	 *            the file
	 * @return true, if it's coffee script
	 */
	private boolean isCoffeeScript(IFile file) {
		return COFFEE_SCRIPT_EXTENSSION.contains(file.getFileExtension());
	}

}
