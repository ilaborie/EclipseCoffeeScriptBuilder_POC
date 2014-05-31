package org.ilaborie.eclipsecon2014.builder;

import static org.ilaborie.eclipsecon2014.builder.CoffeeScriptBuilder.BUILDER_ID;
import static org.ilaborie.eclipsecon2014.builder.ProjectExtension.addBuilder;
import static org.ilaborie.eclipsecon2014.builder.ProjectExtension.removeBuilder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * The CoffeeScript nature
 */
public class CoffeeScriptNature implements IProjectNature {

	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = "org.ilaborie.eclipsecon2014.builder.coffeescript.coffeescriptNature";

	/**
	 * The project
	 */
	private IProject project;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	@Override
	public void configure() throws CoreException {
		addBuilder(this.getProject(), BUILDER_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	@Override
	public void deconfigure() throws CoreException {
		removeBuilder(this.getProject(), BUILDER_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	@Override
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core
	 * .resources.IProject)
	 */
	@Override
	public void setProject(IProject project) {
		this.project = project;
	}
}
