package org.example.kickoff.view.resourcehandler;

import java.net.MalformedURLException;

import javax.faces.FacesException;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;

import org.omnifaces.resourcehandler.DefaultResourceHandler;
import org.omnifaces.util.Faces;

/**
 * Patches Mojarra bug of unnecessarily logging as below when it's about to locate a tagfile.
 * "WARNING JSF1064: Unable to find or serve resource from library, composites"
 */
public class ResourceHandlerImplPatch extends DefaultResourceHandler {

	private static final String COMPOSITE_LIBRARY_NAME = "composites";

	public ResourceHandlerImplPatch(ResourceHandler wrapped) {
		super(wrapped);
	}

	@Override
	public String getLibraryName() {
		return COMPOSITE_LIBRARY_NAME;
	}

	@Override
	public Resource createResourceFromLibrary(String resourceName, String contentType) {
		try {
			if (Faces.getResource("/resources/" + COMPOSITE_LIBRARY_NAME + "/" + resourceName) != null) {
				return getWrapped().createResource(resourceName, COMPOSITE_LIBRARY_NAME, contentType);
			}
			else {
				return null; // So ResourceHandlerImpl with its unnecessary logging will be skipped.
			}
		}
		catch (MalformedURLException e) {
			throw new FacesException(e);
		}
	}

}