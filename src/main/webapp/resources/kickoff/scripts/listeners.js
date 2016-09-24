/*
 * Define all event listeners here.
 * 
 * IMPORTANT: use $(document).on("event", "selector", function) syntax so that it's safe against DOM changes on ajax updates.
 * So: do NOT use $("selector").on("event", function) nor $("selector").event(function) syntaxes for this!
 */

/**
 * Automatically hide any global flash messages.
 */
$(window).on("load", function() {
	kickoff.autohideMessages();
});

/**
 * Allow us to distinguish aborted ajax requests caused by window unload (window close, navigation, synchronous postback, etc).
 */
$(window).on("unload", function() {
	kickoff.windowUnloading();
});

/**
 * Set confirm unload message on change of inputs in non-stateless forms.
 */
$(document).on("change", "form:not(.stateless) :input:not(.stateless)", function() {
	window.onbeforeunload = function() { return $("body").data("unloadmessage"); };
}); OmniFaces.Util.addSubmitListener(function() {
	window.onbeforeunload = null;
});

/**
 * Setup PF ajax progress overlay, disable unload message, toggle form validationFailed class, and autohide messages.
 */
$(document).on("pfAjaxSend", function(event, xhr, options) {
	kickoff.startProgress();
	window.onbeforeunload = null;
}).on("pfAjaxStart", function(event, xhr, options) {
	PrimeFaces.customFocus = true;
}).on("pfAjaxComplete", function(event, xhr, options) {
	kickoff.stopProgress();
	kickoff.autohideMessages();
	$("form.validationFailed").removeClass("validationFailed");

	if (xhr && xhr.pfArgs && options && options.source) {
		var validationFailed = !!xhr.pfArgs.validationFailed;
		$form = $(document.getElementById(options.source)).closest("form");
		
		if (validationFailed) {
			$form.addClass("validationFailed").find(".ui-inputfield.ui-state-error:visible:first").focus();
		}
	}
}).on("pfAjaxError", function(event, xhr, options) {
	kickoff.showServerError();
});

/**
 * Remove field's own highlight and message on entering input.
 */
$(document).on("click keyup change", ".field :input, .field .ui-inputfield", function() {
	var $field = $(this).closest(".field");
	var $label = $field.children("label");
	var $input = $field.find("[id$=_hinput], [id$=_input], :input").first();
	var $error = $field.closest(".ERROR");

	var oldValue = $input.data("oldValue");
	var newValue = $input.val();

	if ($error.length && typeof oldValue === "undefined" && !$input.is(":checkbox")) {
		$input.data("oldValue", oldValue = newValue);
	}

	if (oldValue == newValue) {
		return;
	}

	if ($error.length) {
		$error.removeClass("ERROR").find("ul.messages").hide();
		$error.find(".ui-state-error").removeClass("ui-state-error");
	}

	$input.data("oldValue", newValue);
});