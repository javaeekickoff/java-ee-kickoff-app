package org.example.kickoff.view;

import static java.lang.String.format;
import static java.util.Collections.list;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.omnifaces.util.Servlets.getRequestCookie;
import static org.omnifaces.util.Servlets.getRequestURI;
import static org.omnifaces.util.Servlets.getRequestURIWithQueryString;
import static org.omnifaces.utils.Lang.coalesce;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.example.kickoff.view.filter.LocaleFilter;
import org.example.kickoff.view.viewhandler.LocaleAwareViewHandler;
import org.omnifaces.config.FacesConfigXml;

/**
 * @see LocaleFilter
 * @see LocaleAwareViewHandler
 */
@Named
@RequestScoped
public class ActiveLocale {

	public static final String COOKIE_NAME = "locale";
	public static final int COOKIE_MAX_AGE_IN_DAYS = 30;

	private static final Locale DEFAULT_LOCALE = FacesConfigXml.INSTANCE.getSupportedLocales().get(0);
	private static final LinkedHashMap<Locale, ActiveLocale> SUPPORTED_LOCALES = collectSupportedLocales();

	private Locale locale;
	private String name;
	private String languageTag;
	private String path;
	private List<ActiveLocale> others;

	private ActiveLocale current;
	private boolean explicitlyRequested;
	private boolean defaultLocale;
	private boolean changed;
	private String uri;
	private String canonicalURLFormat;

	@Inject
	private HttpServletRequest request;

	public ActiveLocale() {
		// Keep default c'tor alive for CDI.
	}

	private ActiveLocale(Locale locale) {
		this.locale = locale;
		name = locale.getDisplayLanguage(locale);
		languageTag = locale.toLanguageTag();
		path = locale.equals(DEFAULT_LOCALE) ? "" : ("/" + languageTag);
		current = this;
	}

	@PostConstruct
	public void init() {
		Locale explicitlyRequestedLocale = getExplicitlyRequestedLocale(request);
		Locale cookieRememberedLocale = getCookieRememberedLocale(request);
		Locale clientPreferredLocale = ofNullable(cookieRememberedLocale).orElseGet(() -> getClientRequestedLocale(request));

		current = SUPPORTED_LOCALES.get(coalesce(explicitlyRequestedLocale, clientPreferredLocale));
		defaultLocale = current.locale.equals(DEFAULT_LOCALE);
		explicitlyRequested = explicitlyRequestedLocale != null;
		changed = explicitlyRequested && !clientPreferredLocale.equals(explicitlyRequestedLocale);
		uri = getRequestURIWithQueryString(request).substring(explicitlyRequested ? current.languageTag.length() + 1 : 0);
		canonicalURLFormat = request.getContextPath() + "/%s" + uri;
	}

	public Locale getValue() {
		return current.locale;
	}

	public String getName() {
		return current.name;
	}

	public String getLanguageTag() {
		return current.languageTag;
	}

	public String getPath() {
		return current.path;
	}

	public List<ActiveLocale> getOthers() {
		return current.others;
	}

	public boolean isExplicitlyRequested() {
		return explicitlyRequested;
	}

	public boolean isDefaultLocale() {
		return defaultLocale;
	}

	public boolean isChanged() {
		return changed;
	}

	public String getUri() {
		return uri;
	}

	public String getCanonicalURL() {
		return format(canonicalURLFormat, current.languageTag);
	}

	public String canonicalURL(ActiveLocale otherLocale) {
		return format(canonicalURLFormat, otherLocale.languageTag);
	}

	private static LinkedHashMap<Locale, ActiveLocale> collectSupportedLocales() {
		LinkedHashMap<Locale, ActiveLocale> supportedLocales = FacesConfigXml.INSTANCE.getSupportedLocales().stream()
			.collect(toMap(identity(), ActiveLocale::new, (l, r) -> l, LinkedHashMap::new));
		supportedLocales.values()
			.forEach(supportedLocale -> supportedLocale.others = supportedLocales.values().stream().filter(l -> !l.equals(supportedLocale))
			.collect(toList()));
		return supportedLocales;
	}

	private static Locale getExplicitlyRequestedLocale(HttpServletRequest request) {
		String requestURI = getRequestURI(request) + "/";
		return SUPPORTED_LOCALES.keySet().stream()
			.filter(supportedLocale -> requestURI.startsWith("/" + supportedLocale.toLanguageTag() + "/"))
			.findFirst().orElse(null);
	}

	private static Locale getCookieRememberedLocale(HttpServletRequest request) {
		String cookieRememberedLocale = getRequestCookie(request, COOKIE_NAME);
		return cookieRememberedLocale == null ? null : SUPPORTED_LOCALES.keySet().stream()
			.filter(supportedLocale -> supportedLocale.toLanguageTag().equals(cookieRememberedLocale))
			.findFirst().orElse(null);
	}

	private static Locale getClientRequestedLocale(HttpServletRequest request) {
		return list(request.getLocales()).stream()
			.map(requestedLocale -> SUPPORTED_LOCALES.keySet().stream()
				.filter(supportedLocale -> matches(requestedLocale, supportedLocale))
				.findFirst().orElse(null))
			.filter(Objects::nonNull)
			.findFirst().orElse(DEFAULT_LOCALE);
	}

	private static boolean matches(Locale requestedLocale, Locale supportedLocale) {
		return supportedLocale.equals(requestedLocale)
			|| (supportedLocale.getCountry().isEmpty() && requestedLocale.getLanguage().equals(supportedLocale.getLanguage()));
	}

}