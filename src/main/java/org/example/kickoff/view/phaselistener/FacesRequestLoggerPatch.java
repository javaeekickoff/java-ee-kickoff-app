package org.example.kickoff.view.phaselistener;

import static jakarta.faces.component.behavior.ClientBehaviorContext.BEHAVIOR_EVENT_PARAM_NAME;
import static org.omnifaces.cdi.viewscope.ViewScopeManager.isUnloadRequest;
import static org.omnifaces.config.OmniFaces.OMNIFACES_EVENT_PARAM_NAME;
import static org.omnifaces.util.Components.getActionExpressionsAndListeners;
import static org.omnifaces.util.Components.getCurrentActionSource;
import static org.omnifaces.util.FacesLocal.getRequestParameter;
import static org.omnifaces.util.Utils.coalesce;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;

import org.omnifaces.eventlistener.FacesRequestLogger;

/**
 * Patch FacesRequestLogger to not throw NPE on unload requests. Will be fixed in OmniFaces 3.3.
 */
public class FacesRequestLoggerPatch extends FacesRequestLogger {

	private static final long serialVersionUID = 1L;

	@Override
	protected Map<String, Object> getActionDetails(FacesContext context) {
		UIComponent actionSource = isUnloadRequest(context) ? null : getCurrentActionSource();
		Map<String, Object> actionDetails = new LinkedHashMap<>();
		actionDetails.put("source", actionSource != null ? actionSource.getClientId(context) : null);
		actionDetails.put("event", coalesce(getRequestParameter(context, BEHAVIOR_EVENT_PARAM_NAME), getRequestParameter(context, OMNIFACES_EVENT_PARAM_NAME)));
		actionDetails.put("methods", getActionExpressionsAndListeners(actionSource));
		actionDetails.put("validationFailed", context.isValidationFailed());
		return actionDetails;
	}

}
