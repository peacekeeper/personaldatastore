package pds.web.ui;

import java.util.ArrayList;
import java.util.List;


import nextapp.echo.app.Component;
import nextapp.echo.app.Window;

public class MainWindow extends Window {

	private static final long serialVersionUID = 4065521964923335313L;

	/*
	 * Methods for finding well-known components
	 */

	public static MainWindow findMainWindow(Component childComponent) {

		return (MainWindow) findParentComponentByClass(childComponent, MainWindow.class);
	}

	public static MainContentPane findMainContentPane(Component childComponent) {

		return (MainContentPane) findParentComponentByClass(childComponent, MainContentPane.class);
	}

	public static Component findTopLevelComponent(Component childComponent) {

		if (childComponent == null) return null;

		Component parentComponent = childComponent;
		while (parentComponent.getParent() != null) parentComponent = parentComponent.getParent();

		return parentComponent;
	}

	/*
	 * Methods for finding a parent component
	 * of a given id, render id, class or class name
	 */

	public static Component findParentComponentById(Component childComponent, String id) {

		if (childComponent == null) return null;
		if (childComponent.getId() != null && childComponent.getId().equals(id)) return childComponent;

		return findParentComponentById(childComponent.getParent(), id);
	}

	public static Component findParentComponentByRenderId(Component childComponent, String renderId) {

		if (childComponent == null) return null;
		if (childComponent.getRenderId() != null && childComponent.getRenderId().equals(renderId)) return childComponent;

		return findParentComponentByRenderId(childComponent.getParent(), renderId);
	}

	public static Component findParentComponentByClass(Component childComponent, Class<?> clazz) {

		if (childComponent == null) return null;
		if (clazz.isAssignableFrom(childComponent.getClass())) return childComponent;

		return findParentComponentByClass(childComponent.getParent(), clazz);
	}

	public static Component findParentComponentByClassName(Component childComponent, String className) {

		if (childComponent == null) return null;
		if (childComponent.getClass().getName().equals(className)) return childComponent;

		return findParentComponentByClassName(childComponent.getParent(), className);
	}

	/*
	 * Methods for finding a child component
	 * of a given id, render id, class or class name
	 */

	public static Component findChildComponentById(Component parentComponent, String id) {

		if (parentComponent == null) return null;
		if (parentComponent.getId() != null && parentComponent.getId().equals(id)) return parentComponent;

		for (Component childComponent : parentComponent.getComponents()) {

			Component component = findChildComponentById(childComponent, id);
			if (component != null) return component;
		}

		return null;
	}

	public static Component findChildComponentByRenderId(Component parentComponent, String renderId) {

		if (parentComponent == null) return null;
		if (parentComponent.getRenderId() != null && parentComponent.getRenderId().equals(renderId)) return parentComponent;

		for (Component childComponent : parentComponent.getComponents()) {

			Component component = findChildComponentByRenderId(childComponent, renderId);
			if (component != null) return component;
		}

		return null;
	}

	public static Component findChildComponentByClass(Component parentComponent, Class<?> clazz) {

		if (parentComponent == null) return null;
		if (clazz.isAssignableFrom(parentComponent.getClass())) return parentComponent;

		for (Component childComponent : parentComponent.getComponents()) {

			Component component = findChildComponentByClass(childComponent, clazz);
			if (component != null) return component;
		}

		return null;
	}

	public static Component findChildComponentByClassName(Component parentComponent, String className) {

		if (parentComponent == null) return null;
		if (parentComponent.getClass().getName().equals(className)) return parentComponent;

		for (Component childComponent : parentComponent.getComponents()) {

			Component component = findChildComponentByClassName(childComponent, className);
			if (component != null) return component;
		}

		return null;
	}

	/*
	 * Methods for finding all child components
	 * of a given id, render id, class or class name
	 */

	public static List<Component> findChildComponentsById(Component parentComponent, String id) {

		List<Component> components = new ArrayList<Component> ();

		if (parentComponent == null) return null;
		if (parentComponent.getId() != null && parentComponent.getId().equals(id)) components.add(parentComponent);

		for (Component childComponent : parentComponent.getComponents()) {

			components.addAll(findChildComponentsById(childComponent, id));
		}

		return components;
	}

	public static List<Component> findChildComponentsByRenderId(Component parentComponent, String renderId) {

		List<Component> components = new ArrayList<Component> ();

		if (parentComponent == null) return null;
		if (parentComponent.getRenderId() != null && parentComponent.getRenderId().equals(renderId)) components.add(parentComponent);

		for (Component childComponent : parentComponent.getComponents()) {

			components.addAll(findChildComponentsByRenderId(childComponent, renderId));
		}

		return components;
	}

	public static List<Component> findChildComponentsByClass(Component parentComponent, Class<?> clazz) {

		List<Component> components = new ArrayList<Component> ();

		if (parentComponent == null) return null;
		if (clazz.isAssignableFrom(parentComponent.getClass())) components.add(parentComponent);

		for (Component childComponent : parentComponent.getComponents()) {

			components.addAll(findChildComponentsByClass(childComponent, clazz));
		}

		return components;
	}

	public static List<Component> findChildComponentsByClassName(Component parentComponent, String className) {

		List<Component> components = new ArrayList<Component> ();

		if (parentComponent == null) return null;
		if (parentComponent.getClass().getName().equals(className)) components.add(parentComponent);

		for (Component childComponent : parentComponent.getComponents()) {

			components.addAll(findChildComponentsByClassName(childComponent, className));
		}

		return components;
	}
}
