package org.ilaborie.eclipsecon2014.builder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

/**
 * Project Extension.
 * Provide helpers methods on project (nature, builder)
 */
public final class ProjectExtension {

	private ProjectExtension() {
		super();
	}

	/**
	 * Check the nature.
	 *
	 * @param project
	 *            the project
	 * @param nature
	 *            the nature to check
	 * @return
	 * @throws CoreException
	 *             the core exception
	 */
	public static boolean hasNature(IProject project, String nature)
			throws CoreException {
		if (nature == null) {
			throw new NullPointerException("Expected a nature");
		}
		IProjectDescription desc = project.getDescription();
		// check nature
		return Stream.of(desc.getNatureIds()).anyMatch(
				natureId -> nature.equals(natureId));
	}

	/**
	 * Adds the nature.
	 *
	 * @param project
	 *            the project
	 * @param nature
	 *            the nature to add
	 * @throws CoreException
	 *             the core exception
	 */
	public static void addNature(IProject project, String nature)
			throws CoreException {
		if (nature == null) {
			throw new NullPointerException("Expected a nature");
		}
		IProjectDescription desc = project.getDescription();
		// Grab nature
		List<String> natures = Stream.of(desc.getNatureIds())
				.filter(natureId -> !nature.equals(natureId))
				.collect(Collectors.toList());

		// Add the Nature
		natures.add(nature);

		// Update Project
		desc.setNatureIds(natures.toArray(new String[natures.size()]));
		project.setDescription(desc, null);
	}

	/**
	 * Removes the nature.
	 *
	 * @param project
	 *            the project
	 * @param nature
	 *            the nature to remove
	 * @throws CoreException
	 *             the core exception
	 */
	public static void removeNature(IProject project, String nature)
			throws CoreException {
		if (nature == null) {
			throw new NullPointerException("Expected a nature");
		}
		IProjectDescription desc = project.getDescription();
		// Grab nature
		List<String> natures = Stream.of(desc.getNatureIds())
				.filter(natureId -> !nature.equals(natureId))
				.collect(Collectors.toList());

		// Update Project
		desc.setNatureIds(natures.toArray(new String[natures.size()]));
		project.setDescription(desc, null);
	}

	/**
	 * Adds a builder to a project.
	 *
	 * @param project
	 *            the project
	 * @param builder
	 *            the builder to add
	 * @throws CoreException
	 *             the core exception
	 */
	public static void addBuilder(IProject project, String builder)
			throws CoreException {
		if (builder == null) {
			throw new NullPointerException("Expected a builder");
		}
		IProjectDescription desc = project.getDescription();
		// Grab commands
		List<ICommand> commands = Stream.of(desc.getBuildSpec())
				.filter(cmd -> !builder.equals(cmd.getBuilderName()))
				.collect(Collectors.toList());

		// Add the Builder
		ICommand command = desc.newCommand();
		command.setBuilderName(builder);
		commands.add(command);

		// Update Project
		desc.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
		project.setDescription(desc, null);
	}

	/**
	 * Removes a nature of project.
	 *
	 * @param project
	 *            the project
	 * @param builder
	 *            the nature to remove
	 * @throws CoreException
	 *             the core exception
	 */
	public static void removeBuilder(IProject project, String builder)
			throws CoreException {
		if (builder == null)
			throw new NullPointerException("Expected a builder");
		IProjectDescription desc = project.getDescription();
		// Grab commands
		List<ICommand> commands = Stream.of(desc.getBuildSpec())
				.filter(cmd -> !builder.equals(cmd.getBuilderName()))
				.collect(Collectors.toList());

		// Update Project
		desc.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
		project.setDescription(desc, null);
	}

}
