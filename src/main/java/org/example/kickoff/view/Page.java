package org.example.kickoff.view;

import static org.omnifaces.util.Faces.getResource;
import static org.omnifaces.util.Faces.getViewId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

import org.omnifaces.config.WebXml;

@Named
@Dependent
public class Page {

	private static final Map<String, Page> PAGES = new ConcurrentHashMap<>();

	private Page current;
	private String path;
	private String name;
	private boolean home;

	public Page() {
		// Keep default c'tor alive for CDI.
	}

	private Page(String path) {
		this.path = path;
		String uri = "/" + path;

		while (!uri.isEmpty()) {
			try {
				getResource(uri + ".xhtml").toString();
				break;
			}
			catch (Exception ignore) {
				uri = uri.substring(0, uri.lastIndexOf('/'));
			}
		}

		name = path.replaceFirst("WEB\\-INF/", "").replaceAll("\\W+", "_");
		home = WebXml.INSTANCE.getWelcomeFiles().contains(path);
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

	public String getPath() {
		return current.path;
	}

	public String getName() {
		return current.name;
	}

	public boolean isHome() {
		return current.home;
	}

}