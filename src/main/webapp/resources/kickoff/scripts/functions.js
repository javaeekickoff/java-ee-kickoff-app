/*
 * Define all custom functions here. 
 * 
 * IMPORTANT: use a custom namespace so they don't pollute global scope!
 */
var kickoff = function(document) {

	var self = {};
	var windowUnloading;

	self.windowUnloading = function() {
		windowUnloading = true;
	}

	self.startProgress = function() {
		$("html").addClass("progress");
		$(document).trigger("kStartProgress");
	}

	self.stopProgress = function(selector) {
		$(document).trigger("kStopProgress", selector);
		$("html").removeClass("progress");
	}

	self.inProgress = function() {
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

	self.showServerError = function() {
		self.showMessage("The server did not respond as expected!", "fatal");
	}

	self.autohideMessages = function() {
		var $messages = $("#messages > div");

		if (!$messages.length || $messages.hasClass("ui-messages-fatal")) { // Don't autohide fatal messages.
			return;
		}

		var wordCount = $messages.text().split(/\W/).length;

		// First 3 secs "warming up" time (i.e. give human eye chance to find/focus/understand message dialog) and then 200ms per word.
		var readingTimeMillis = 3000 + (wordCount * 200);

		setTimeout(function() {
			$messages.slideUp();
		}, readingTimeMillis);
	}

	self.pfOnsuccess = function(callback) {
		var args = arguments.callee.caller.arguments;

		if (args && args[2] && !args[2].validationFailed) {
			callback($(document.getElementById(args[0].pfSettings.source)));
		}
	}

	return self;

}(document);