package org.example.kickoff.view;

import static org.omnifaces.util.Faces.getResource;
import static org.omnifaces.util.Faces.getViewId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Named;

@Named
public class Page {

	private static final Map<String, Page> PAGES = new ConcurrentHashMap<>();

	private Page current;
	private String path;
	private String name;

	public Page() {
		// Keep default c'tor alive.
	}

	private Page(String path) {
		try {
			getResource("/" + path + ".xhtml").toString();
		}
		catch (Exception e) {
			throw new IllegalArgumentException(path, e);
		}

		this.path = path;
		name = path.replaceFirst("WEB\\-INF/", "").replaceAll("\\W+", "_");
		current = this;
	}

	@PostConstruct
	public void init() {
		String viewId = getViewId();
		current = get(viewId.substring(1, viewId.lastIndexOf('.')));
	}

	public Page get(String path) {
		return PAGES.computeIfAbsent(path, k -> new Page(path));
	}

	public boolean is(String path) {
		return path.equals(current.path);
	}

	public String getName() {
		return current.name;
	}

	public String getPath() {
		return current.path;
	}

}