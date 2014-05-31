package org.ilaborie.eclipsecon2014.builder.handlers;

import static org.ilaborie.eclipsecon2014.builder.ProjectExtension.addNature;
import static org.ilaborie.eclipsecon2014.builder.ProjectExtension.hasNature;
import static org.ilaborie.eclipsecon2014.builder.ProjectExtension.removeNature;
import static org.ilaborie.eclipsecon2014.builder.CoffeeScriptNature.NATURE_ID;

import java.util.stream.Stream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * The toggle CoffeeScript nature handler.
 */
public class ToggleCoffeeScriptNature extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		//
		if (selection instanceof IStructuredSelection) {
			Stream.of(((IStructuredSelection) selection).toArray())
					.map(element -> {
						IProject project = null;
						if (element instanceof IProject) {
							project = (IProject) element;
						} else if (element instanceof IAdaptable) {
							project = (IProject) ((IAdaptable) element)
									.getAdapter(IProject.class);
						}
						return project;
					}).filter(prj -> prj != null).forEach(prj -> {
						try {
							if (hasNature(prj, NATURE_ID)) {
								removeNature(prj, NATURE_ID);
							} else {
								addNature(prj, NATURE_ID);
							}
						} catch (Exception e) {
							throw new RuntimeException("WTF !");
						}
					});
		}

		return null;
	}

}