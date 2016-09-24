package org.example.kickoff.view.resourcehandler;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;

import org.omnifaces.resourcehandler.DefaultResourceHandler;

/**
 * Patches Mojarra bug of unnecessarily logging
 * WARNING JSF1064: Unable to find or serve resource from library, kickoff/composites
 */
public class ResourceHandlerImplPatch extends DefaultResourceHandler {

	public ResourceHandlerImplPatch(ResourceHandler wrapped) {
		super(wrapped);
	}

	/**
	 * Returns our composite library name as defined in kickoff.taglib.xml.
	 */
	@Override
	public String getLibraryName() {
		return "kickoff/composites";
	}

	/**
	 * This override returns null instead of delegating to wrapper which eventually ends up in
	 * Mojarra's ResourceHandlerImpl which thus logs the unnecessary warning.
	 */
	@Override
	public Resource createResourceFromLibrary(String resourceName, String contentType) {
		return null;
	}

}