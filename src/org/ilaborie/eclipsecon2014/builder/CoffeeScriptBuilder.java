package org.ilaborie.eclipsecon2014.builder;

import java.util.Map;

import javax.script.ScriptException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class CoffeeScriptBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "org.ilaborie.eclipsecon2014.builder.coffeescript.coffeescriptBuilder";

	/** CoffeeScript marker type */
	public static final String MARKER_TYPE = "org.ilaborie.eclipsecon2014.builder.coffeescript.coffeescriptProblem";

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
		if (kind == FULL_BUILD) {
			this.getProject().accept(resources -> {
				compile(resources);
				return true;
			});
		} else {
			IResourceDelta delta = this.getDelta(this.getProject());
			if (delta == null) {
				// Full Build
				this.getProject().accept(resources -> {
					compile(resources);
					return true;
				});
			} else {
				delta.accept(d -> {
					IResource resource = d.getResource();
					switch (d.getKind()) {
					case IResourceDelta.CHANGED:
					case IResourceDelta.ADDED:
						compile(resource);
						break;
					default: // do Nothing
					}
					return true;
				});
			}
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
	 * @return the string
	 */
	void compile(IResource resource) {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			String compiled;
			try {
				compiled = CoffeeScriptCompiler.compileCoffeeScript(file);
				if (compiled != null) {
					// FIXME write to destination
					System.out.println(compiled);
				}
			} catch (ScriptException e) {
				addMarker(file, e.toString(), -1, IMarker.SEVERITY_ERROR);
			}
		}
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
	 */
	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	/**
	 * Delete markers.
	 *
	 * @param file
	 *            the file
	 */
	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

}
