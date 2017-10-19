/*
 * Define all custom functions and global initialization here. 
 * 
 * IMPORTANT: use a custom namespace so they don't pollute global scope!
 */
var kickoff = function(window, document) {


	// Properties -----------------------------------------------------------------------------------------------------

	var self = {};
	var currentMedia;
	var windowUnloading;


	// Public functions -----------------------------------------------------------------------------------------------

	self.isDesktop = function() {
	    return currentMedia == "desktop";
	}

	self.isTablet = function() {
	    return currentMedia == "tablet";
	}

	self.isMobile = function() {
	    return currentMedia == "mobile";
	}

	self.isInProgress = function() {
		return $("html").hasClass("progress");
	}

	self.showMessage = function(message, severity) {
		if (severity == "fatal") {
			message += "<br/>Please <a href='#' onclick='location.reload(true)'>reload</a> and try again.";
		}
		else if (!severity) {
			severity = "info";
		}

		$("#messages").html("<div class='ui-messages-" + severity + " ui-corner-all'>"
			+ "<a href='#' class='ui-messages-close' onclick='$(this).parent().fadeOut();return false;'>"
			+ "<span class='ui-icon ui-icon-close'></span></a><span class='ui-messages-" + severity + "-icon'></span>"
			+ "<ul><li><span class='ui-messages-" + severity + "-summary'>"
			+ message
			+ "</span></li></ul></div>");
	}

	self.pfOnsuccess = function(callback) {
		var args = arguments.callee.caller.arguments;

		if (args && args[2] && !args[2].validationFailed) {
			callback($(document.getElementById(args[0].pfSettings.source)));
		}
	}


	// Private functions ----------------------------------------------------------------------------------------------
	
	var hideGlobalMessages = function() {
		$("#messages > div").slideUp();
	};
	
	var autoHideGlobalMessages = function() {
		var $messages = $("#messages > div");

		if (!$messages.length || $messages.hasClass("ui-messages-fatal")) { // Don't autohide fatal messages.
			return;
		}

		var wordCount = $messages.text().split(/\W/).length;
		var readingTimeMillis = 3000 + (wordCount * 200); // First 3 secs "warming up" time (i.e. give human eye chance to find/focus/understand message dialog) and then 200ms per word.

		setTimeout(hideGlobalMessages, readingTimeMillis);
		$("#messages").on("click", hideGlobalMessages);
	};

	var getSubmittingForm = function(source) {
		var sourceClientId = (typeof source === "object") ? source.id : source;
		var $form = $(document.getElementById(sourceClientId)).closest("form");

		while (!$form.length && sourceClientId.indexOf(":") > 0) { // May happen when source disappears after a conditional rendering.
			sourceClientId = sourceClientId.substring(0, sourceClientId.lastIndexOf(":"));
			$form = $(document.getElementById(sourceClientId)).closest("form");
		}

		return $form;
	};


	// Global initialization ------------------------------------------------------------------------------------------

	/**
	 * Set default jQuery UI animation speed to 300ms.
	 */
	$.fx.speeds._default = 300;

	/**
	 * Setup window events.
	 * - On window load, autohide global (flash) messa.ges.
	 * - On window load, resize and orientationchange, trigger custom window event 'mediachange' when CSS media changes, and trigger custom window event 'render'.
	 * - On window unload, mark state so that any ajax errors caused by window unload won't trigger server error.
	 */
	$(window).on("load", function() {
		autoHideGlobalMessages();
	}).on("load resize orientationchange", function() {
		var media = window.getComputedStyle ? window.getComputedStyle(document.body, ":after").content.replace(/"/g, "") : "desktop";

		if (media != currentMedia) {
			currentMedia = media;
			$(window).trigger("mediachange", media);
		}

		$(window).trigger("render", media);
	}).on("unload", function() {
		windowUnloading = true;
	});

	/**
	 * Setup unload events. 
	 * - On change of non-stateless inputs in non-stateless forms, mark unsaved changes.
	 * - On submit of any PrimeFaces ajax form in the document, unmark unsaved changes.
	 * - On submit of any synchronous form in the document, unmark unsaved changes.
	 * - On window beforeunload, if there are any unsaved changes, show the unload message defined as <body data-unloadmessage>.
	 * This explicitly uses window.onbeforeunload instead of $(window).on("beforeunload") or window.addEventListener("beforeunload"), 
	 * so OmmiFaces ViewScoped unload script will continue to work properly.
	 */
	$(document).on("change", "form:not(.stateless) :input:not(.stateless):not(.ui-column-filter)", function() {
		$("body").data("unsavedchanges", true);
	}).on("pfAjaxComplete", function() {
		$("body").data("unsavedchanges", false);
	}); OmniFaces.Util.addSubmitListener(function() {
		$("body").data("unsavedchanges", false);
	}); window.onbeforeunload = function() {
		return $("body").data("unsavedchanges") ? $("body").data("unloadmessage") : null;
	};

	/**
	 * Setup PrimeFaces ajax events.
	 * - On ajax start, disable PF built in focus listener which refocuses last active element (as we already have our own focus logic via o:highlight).
	 * - On ajax send, trigger progress overlay and custom document event 'startProgress'.
	 * - On ajax success, abort all PF ajax requests currently in queue (double submit prevention when e.g. submit+blur are invoked simultaneously).
	 * - On ajax error, show fatal server error message (only if windows is not unloading).
	 * - On ajax complete, toggle 'validationFailed' class on parent form if necessary, trigger custom document event 'stopProgress', hide overlay and autohide global messages.
	 */
	$(document).on("pfAjaxStart", function(event) {
		PrimeFaces.customFocus = true;
	}).on("pfAjaxSend", function(event, xhr, options) {
		$("html").addClass("progress");
		$(document).trigger("startProgress");
	}).on("pfAjaxSuccess", function(event, xhr, options) {
		PrimeFaces.ajax.Queue.abortAll();
	}).on("pfAjaxError", function(event, xhr, options) {
		if (!windowUnloading) {
			self.showMessage("The server did not respond as expected!", "fatal");
		}
	}).on("pfAjaxComplete", function(event, xhr, options) {
		$("form.validationFailed").removeClass("validationFailed");

		if (xhr && xhr.pfArgs && options && options.source) {
			getSubmittingForm(options.source).toggleClass("validationFailed", !!xhr.pfArgs.validationFailed);
		}

		if (self.isInProgress()) {
			$(document).trigger("stopProgress");
			$("html").removeClass("progress");
		}

		autoHideGlobalMessages();
	});

	return self;

}(window, document);