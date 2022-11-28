package org.example.kickoff.view.viewhandler;

import static org.omnifaces.util.Beans.getReference;
import static org.omnifaces.util.FacesLocal.getRequestContextPath;

import jakarta.faces.application.ViewHandler;
import jakarta.faces.application.ViewHandlerWrapper;
import jakarta.faces.context.FacesContext;

import org.example.kickoff.view.ActiveLocale;

public class LocaleAwareViewHandler extends ViewHandlerWrapper {

	private ViewHandler wrapped;

	public LocaleAwareViewHandler(ViewHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public String getActionURL(FacesContext context, String viewId) {
		String contextPath = getRequestContextPath(context);
		String localePath = getReference(ActiveLocale.class).getPath();
		String uri = super.getActionURL(context, viewId).substring(contextPath.length());
		return contextPath + localePath + uri;
	}

	@Override
	public ViewHandler getWrapped() {
		return wrapped;
	}
}
