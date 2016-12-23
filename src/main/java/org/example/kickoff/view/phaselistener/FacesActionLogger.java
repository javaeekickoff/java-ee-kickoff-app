package org.example.kickoff.view.phaselistener;

import static java.lang.System.nanoTime;
import static java.util.Collections.max;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.stream.Collectors.toList;
import static javax.faces.event.PhaseId.ANY_PHASE;
import static javax.faces.event.PhaseId.RESTORE_VIEW;
import static javax.faces.render.ResponseStateManager.VIEW_STATE_PARAM;
import static org.omnifaces.cdi.viewscope.ViewScopeManager.isUnloadRequest;
import static org.omnifaces.util.Components.getActionExpressionsAndListeners;
import static org.omnifaces.util.Components.getCurrentActionSource;
import static org.omnifaces.util.FacesLocal.getRemoteAddr;
import static org.omnifaces.util.FacesLocal.getRemoteUser;
import static org.omnifaces.util.FacesLocal.getRequestAttribute;
import static org.omnifaces.util.FacesLocal.getRequestParameter;
import static org.omnifaces.util.FacesLocal.getRequestParameterValuesMap;
import static org.omnifaces.util.FacesLocal.getRequestURIWithQueryString;
import static org.omnifaces.util.FacesLocal.getSessionId;
import static org.omnifaces.util.FacesLocal.isRenderResponse;
import static org.omnifaces.util.FacesLocal.setRequestAttribute;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.omnifaces.eventlistener.DefaultPhaseListener;

public class FacesActionLogger extends DefaultPhaseListener {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(FacesActionLogger.class.getName());

	public FacesActionLogger() {
		super(PhaseId.ANY_PHASE);
	}

	@Override
	public void beforePhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();

        if (!shouldHandlePhase(context)) {
        	return;
        }

        getPhaseTimer(context).start(event.getPhaseId());
	}

	@Override
    public void afterPhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();

        if (!shouldHandlePhase(context)) {
        	return;
        }

        getPhaseTimer(context).stop(event.getPhaseId());

		if (!(isRenderResponse(context) || context.getResponseComplete())) {
			return;
		}

		try {
	        Map<String, Object> log = new LinkedHashMap<>();
	        log.put("url", getRequestURIWithQueryString(context));
	        log.put("user", getUserDetails(context));
	        log.put("action", getActionDetails(context));
	        log.put("params", getFilteredParams(context));
	        log.put("errors", getFacesMessages(context));
	        log.put("timer", getPhaseTimer(context));
	        logger.log(INFO, log.toString());
		}
		catch (Exception e) {
			logger.log(SEVERE, "Logging failed", e);
		}
    }

	private static boolean shouldHandlePhase(FacesContext context) {
		return context.isPostback() && !isUnloadRequest(context);
	}

	private static Map<String, Object> getUserDetails(FacesContext context) {
        Map<String, Object> userDetails = new LinkedHashMap<>();
        userDetails.put("ip", getRemoteAddr(context));
        userDetails.put("email", getRemoteUser(context));
        userDetails.put("session", getSessionId(context));
        userDetails.put("viewState", getRequestParameter(context, VIEW_STATE_PARAM));
        return userDetails;
	}

	private static Map<String, Object> getActionDetails(FacesContext context) {
        UIComponent actionSource = getCurrentActionSource();
        Map<String, Object> actionDetails = new LinkedHashMap<>();
        actionDetails.put("source", actionSource != null ? actionSource.getClientId(context) : null);
        actionDetails.put("event", getRequestParameter(context, "javax.faces.behavior.event"));
        actionDetails.put("methods", getActionExpressionsAndListeners(actionSource));
        actionDetails.put("validationFailed", context.isValidationFailed());
        return actionDetails;
	}

	private static Map<String, String> getFilteredParams(FacesContext context) {
		Set<Entry<String, String[]>> params = getRequestParameterValuesMap(context).entrySet();
		Map<String, String> filteredParams = new TreeMap<>();

		for (Entry<String, String[]> entry : params) {
			String name = entry.getKey();

			if (name.startsWith("javax.faces.")) {
				continue; // JSF internal stuff is not interesting.
			}

			String[] values = entry.getValue();
			String value = (values == null) ? null : (values.length == 1) ? values[0] : Arrays.toString(values);

			if (value != null && name.endsWith("assword")) {
				value = "********"; // Mask passwords.
			}

			filteredParams.put(name, value);
		}

		return filteredParams;
	}

	private static Map<String, List<String>> getFacesMessages(FacesContext context) {
		Map<String, List<String>> facesMessages = new TreeMap<>();

		context.getClientIdsWithMessages().forEachRemaining(clientId -> {
			facesMessages.put(String.valueOf(clientId), context.getMessageList(clientId).stream().map(message -> message.getSummary()).collect(toList()));
		});

		return facesMessages;
	}

	private static PhaseTimer getPhaseTimer(FacesContext context) {
		PhaseTimer timer = getRequestAttribute(context, PhaseTimer.class.getName());

		if (timer == null) {
			timer = new PhaseTimer();
			setRequestAttribute(context, PhaseTimer.class.getName(), timer);
		}

		return timer;
	}

	private static class PhaseTimer {

		private Map<Integer, Long> startTimes = new HashMap<>();
		private Map<Integer, Long> endTimes = new HashMap<>();

		public void start(PhaseId phaseId) {
			startTimes.putIfAbsent(phaseId.getOrdinal(), nanoTime());
		}

		public void stop(PhaseId phaseId) {
			endTimes.put(phaseId.getOrdinal(), nanoTime());
		}

		public String getDuration(PhaseId phase) {
			Long startTime = startTimes.get(phase == ANY_PHASE ? RESTORE_VIEW.getOrdinal() : phase.getOrdinal());
			Long endTime = endTimes.get(phase == ANY_PHASE ? max(endTimes.keySet()) : phase.getOrdinal());
			return (startTime != null && endTime != null ? ((endTime - startTime) / 1_000_000) : -1) + "ms";
		}

		@Override
		public String toString() {
			Map<Integer, String> duration = new TreeMap<>();

			for (PhaseId phase : PhaseId.VALUES) {
				duration.put(phase.getOrdinal(), getDuration(phase));
			}

			return duration.toString();
		}

	}

}