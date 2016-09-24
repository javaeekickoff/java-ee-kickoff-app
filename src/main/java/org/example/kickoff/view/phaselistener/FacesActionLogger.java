package org.example.kickoff.view.phaselistener;

import static org.omnifaces.util.Components.getActionExpressionsAndListeners;
import static org.omnifaces.util.Components.getCurrentActionSource;
import static org.omnifaces.util.FacesLocal.getRemoteAddr;
import static org.omnifaces.util.FacesLocal.getRemoteUser;
import static org.omnifaces.util.FacesLocal.getRequestParameterValuesMap;
import static org.omnifaces.util.FacesLocal.getRequestURLWithQueryString;
import static org.omnifaces.util.FacesLocal.getSessionId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.omnifaces.eventlistener.DefaultPhaseListener;

public class FacesActionLogger extends DefaultPhaseListener {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(FacesActionLogger.class.getName());

	private static final String FORMAT_LOG = "ACTION - %s, %s, %s";
	private static final String FORMAT_REQUEST = "Request[url=%s, ip=%s, session=%s, user=%s]";
	private static final String FORMAT_ACTION = "Action[method=%s, validationFailed=%s]";
	private static final String FORMAT_PARAMS = "Params%s";

	public FacesActionLogger() {
		super(PhaseId.PROCESS_VALIDATIONS);
	}

	@Override
    public void afterPhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();

        if (!context.isPostback()) {
        	return;
        }

        logger.info(String.format(FORMAT_LOG,
    		String.format(FORMAT_REQUEST, getRequestURLWithQueryString(context), getRemoteAddr(context), getSessionId(context), getRemoteUser(context)),
    		String.format(FORMAT_ACTION, getActionExpressionsAndListeners(getCurrentActionSource()), context.isValidationFailed()),
    		String.format(FORMAT_PARAMS, getFilteredParams(context))
        ));
    }

	private static List<String> getFilteredParams(FacesContext context) {
		Set<Entry<String, String[]>> params = getRequestParameterValuesMap(context).entrySet();
		List<String> filteredParams = new ArrayList<>(params.size());

		for (Entry<String, String[]> entry : params) {
			String name = entry.getKey();

			if (name.startsWith("javax.faces.") && !name.startsWith("javax.faces.ViewState")) {
				continue; // JSF internal stuff except of view state is not interesting.
			}

			String[] values = entry.getValue();
			String value = (values == null) ? null : (values.length == 1) ? values[0] : Arrays.toString(values);

			if (value != null && (name.endsWith(":password") || name.endsWith("Password"))) {
				value = value.replaceAll("(?s).", "*"); // Mask passwords.
			}

			filteredParams.add(name + (name.equals(value) ? "" : ("=" + value)));
		}

		return filteredParams;
	}

}