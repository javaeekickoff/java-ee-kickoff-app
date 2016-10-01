/*
 * Define all custom functions and global initialization here. 
 * 
 * IMPORTANT: use a custom namespace so they don't pollute global scope!
 */
var kickoff = function(window, document) {


	// "Constants" ----------------------------------------------------------------------------------------------------

	var DEFAULT_ANIMATION_SPEED = 300;

	
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
		if (!windowUnloading) {
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
	}

	self.pfOnsuccess = function(callback) {
		var args = arguments.callee.caller.arguments;

		if (args && args[2] && !args[2].validationFailed) {
			callback($(document.getElementById(args[0].pfSettings.source)));
		}
	}


	// Private functions ----------------------------------------------------------------------------------------------
	
	function autohideGlobalMessages() {
		var $messages = $("#messages > div");

		if (!$messages.length || $messages.hasClass("ui-messages-fatal")) { // Don't autohide fatal messages.
			return;
		}

		var wordCount = $messages.text().split(/\W/).length;

		// First 3 secs "warming up" time (i.e. give human eye chance to find/focus/understand message dialog) and then 200ms per word.
		var readingTimeMillis = 3000 + (wordCount * 200);

		setTimeout(function() {
			$messages.slideUp(DEFAULT_ANIMATION_SPEED);
		}, readingTimeMillis);
	}


	// Global initialization ------------------------------------------------------------------------------------------

	/**
	 * Setup window events.
	 */
	$(window).on("load", function() {
		autohideGlobalMessages();
	}).on("resize orientationchange load", function() {
		var media = window.getComputedStyle ? window.getComputedStyle(document.body, ":after").content.replace(/"/g, "") : "desktop";

		if (media != currentMedia) {
			currentMedia = media;
			$(window).trigger("mediachange", media);
		}
	}).on("unload", function() {
		windowUnloading = true;
	});

	/**
	 * Setup confirm unload message.
	 */
	$(document).on("change", "form:not(.stateless) :input:not(.stateless)", function() {
		window.onbeforeunload = function() { return $("body").data("unloadmessage"); };
	}); OmniFaces.Util.addSubmitListener(function() {
		window.onbeforeunload = null;
	});

	/**
	 * Setup PrimeFaces ajax progress behavior.
	 */
	$(document).on("pfAjaxStart", function(event) {
		$("html").addClass("progress");
		$(document).trigger("kStartProgress");
		PrimeFaces.customFocus = true;
	}).on("pfAjaxSuccess", function(event, xhr, options) {
		PrimeFaces.ajax.Queue.abortAll();
	}).on("pfAjaxError", function(event, xhr, options) {
		self.showMessage("The server did not respond as expected!", "fatal");
	}).on("pfAjaxComplete", function(event, xhr, options) {
		$("form.validationFailed").removeClass("validationFailed");

		if (xhr && xhr.pfArgs && options && options.source) {
			var $form = $(document.getElementById(options.source)).closest("form");
			$form.toggleClass("validationFailed", !!xhr.pfArgs.validationFailed);
		}

		window.onbeforeunload = null;
		$(document).trigger("kStopProgress", selector);
		$("html").removeClass("progress");
		autohideGlobalMessages();
	});

	return self;

}(window, document);