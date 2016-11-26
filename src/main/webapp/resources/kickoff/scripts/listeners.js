/*
 * Define all document event listeners here.
 * 
 * IMPORTANT: use $(document).on("event", "selector", function) syntax so that it's safe against DOM changes on ajax updates.
 * So: do NOT use $("selector").on("event", function) nor $("selector").event(function) syntaxes for this!
 */

/**
 * Remove field's own highlight and message on entering <k:input> value.
 */
$(document).on("click keyup change", ".field :input, .field .ui-inputfield", function() {
	var $input = $(this);
	var $field = $input.closest(".field");
	var $label = $field.children("label");
	var $error = $field.closest(".ERROR");

	var oldValue = $input.data("oldValue");
	var newValue = $input.val();

	if ($error.length && typeof oldValue === "undefined" && !$input.is(":checkbox") && !$input.is("select")) {
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

/**
 * Close hamburger menu when main section is clicked.
 */
$(document).on("click", "main", function() {
	if (kickoff.isMobile()) {
		$("#hamburger:checked").prop("checked", false);
	}
});