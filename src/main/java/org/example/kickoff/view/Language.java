package org.example.kickoff.view;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static org.omnifaces.util.Faces.addResponseCookie;
import static org.omnifaces.util.Faces.refreshWithQueryString;
import static org.omnifaces.util.Faces.removeResponseCookie;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.Cookie;
import org.omnifaces.config.FacesConfigXml;
import org.omnifaces.util.Faces;

@Named
@RequestScoped
public class Language {

	private static final List<Locale> SUPPORTED_LOCALES = FacesConfigXml.INSTANCE.getSupportedLocales();
	private static final String COOKIE_NAME = "language";
	private static final String COOKIE_PATH = "/";
	private static final int COOKIE_MAX_AGE = (int) DAYS.toSeconds(30);

	@Inject @Cookie
	private String language;
	private Locale locale;
	private List<String> others;

	@PostConstruct
	public void init() {
		if (language != null && SUPPORTED_LOCALES.stream().anyMatch(l -> l.getLanguage().equals(language))) {
			locale = new Locale(language);
		}
		else {
			locale = Faces.getLocale();

			if (language != null) {
				removeResponseCookie(COOKIE_NAME, COOKIE_PATH);
			}

			language = locale.getLanguage();
		}

		others = SUPPORTED_LOCALES.stream().map(l -> l.getLanguage()).filter(l -> !l.equals(language)).collect(toList());
	}

	public void switchTo(String language) throws IOException {
		addResponseCookie(COOKIE_NAME, language, COOKIE_PATH, COOKIE_MAX_AGE);
		refreshWithQueryString();
	}

	public String getCode() {
		return language;
	}

	public Locale getLocale() {
		return locale;
	}

	public List<String> getOthers() {
		return others;
	}

}