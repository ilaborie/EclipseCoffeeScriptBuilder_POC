package org.ilaborie.eclipsecon2014.builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import javax.script.ScriptException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public class CoffeeScriptBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "org.ilaborie.eclipsecon2014.builder.coffeescript.coffeescriptBuilder";

	/** CoffeeScript marker type */
	public static final String MARKER_TYPE = "org.ilaborie.eclipsecon2014.builder.coffeescript.coffeescriptProblem";

	private final CoffeeScriptCompiler csCompiler;

	/**
	 * Instantiates a new coffee script builder.
	 */
	public CoffeeScriptBuilder() {
		super();
		this.csCompiler = new CoffeeScriptCompiler();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		IResourceDelta delta = null;
		boolean fullBuild = (kind == FULL_BUILD) 
				|| ((delta = this.getDelta(this.getProject())) == null);

		if (fullBuild) {
			// Full Build
			this.getProject().accept(resources -> {
				compile(resources, monitor);
				return true;
			});
		} else {
			// Delta Build
			delta.accept(d -> {
				IResource resource = d.getResource();
				switch (d.getKind()) {
				case IResourceDelta.CHANGED:
				case IResourceDelta.ADDED:
					compile(resource, monitor);
					break;
				default: // do Nothing
				}
				return true;
			});
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IncrementalProjectBuilder#clean(org.eclipse
	 * .core.runtime.IProgressMonitor)
	 */
	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		this.getProject().deleteMarkers(MARKER_TYPE, true,
				IResource.DEPTH_INFINITE);
	}

	/**
	 * Compile coffeescript.
	 *
	 * @param resource
	 *            the resource
	 * @param monitor
	 *            the progress monitor
	 * @return the string
	 * @throws CoreException
	 */
	void compile(IResource resource, IProgressMonitor monitor)
			throws CoreException {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			this.deleteMarkers(file);
			try {
				// Compile
				Optional<String> compiled = csCompiler.compileCoffeeScript(
						file, monitor);
				if (compiled.isPresent()) {
					IFile out = getCompiledFile(file);
					// Write to destination
					try (InputStream source = new ByteArrayInputStream(compiled
							.get().getBytes(StandardCharsets.UTF_8))) {
						if (out.exists()) {
							out.setContents(source, IResource.FORCE, monitor);
						} else {
							out.create(source, IResource.FORCE, monitor);
						}
					}
				}
			} catch (ScriptException e) {
				// FIXME mapping file number ???
				this.addMarker(file, e.getLocalizedMessage(),
						e.getLineNumber(), IMarker.SEVERITY_ERROR);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Gets the compiled file.
	 *
	 * @param file
	 *            the file
	 * @return the compiled file
	 */
	private IFile getCompiledFile(IFile file) {
		String name = file.getName();
		name = name.substring(0, name.length()
				- file.getFileExtension().length())
				+ "js";
		return file.getParent().getFile(new Path(name));
	}

	/**
	 * Adds a marker.
	 *
	 * @param file
	 *            the file
	 * @param message
	 *            the message
	 * @param lineNumber
	 *            the line number
	 * @param severity
	 *            the severity
	 * @throws CoreException
	 */
	private void addMarker(IFile file, String message, int lineNumber,
			int severity) throws CoreException {
		IMarker marker = file.createMarker(MARKER_TYPE);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.SEVERITY, severity);
		marker.setAttribute(IMarker.LINE_NUMBER, lineNumber < 0 ? 1
				: lineNumber);

	}

	/**
	 * Delete markers.
	 *
	 * @param file
	 *            the file
	 * @throws CoreException
	 */
	private void deleteMarkers(IFile file) throws CoreException {
		file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
	}

}
