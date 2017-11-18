/*
 * Define all PrimeFaces patches here.
 */

/**
 * Fix <p:slider> to format number with correct separator character in output value.
 * Original source here: https://github.com/primefaces/primefaces/blob/master/src/main/resources/META-INF/resources/primefaces/slider/slider.js
 */
if (PrimeFaces.widget.Slider) {
	PrimeFaces.widget.Slider = PrimeFaces.widget.Slider.extend({
		onSlide: function(event, ui) {
			this._super(event, ui);
			this.output.text(parseInt(this.output.text()).toLocaleString(PrimeFaces.settings.locale));
		}
	});
}

/**
 * Fix <p:spinner> to add missing ui-widget class to input element.
 * Original source here: https://github.com/primefaces/primefaces/blob/master/src/main/resources/META-INF/resources/primefaces/spinner/spinner.js
 */
if (PrimeFaces.widget.Spinner) {
	PrimeFaces.widget.Spinner = PrimeFaces.widget.Spinner.extend({
		init: function(cfg) {
			this._super(cfg);
			this.input.addClass("ui-widget");
		}
	});
}

/**
 * Fix <p:selectBooleanCheckbox> to actually be hovered/toggled when associated label is hovered/clicked.
 * Original source here: https://github.com/primefaces/primefaces/blob/master/src/main/resources/META-INF/resources/primefaces/forms/forms.js
 */
if (PrimeFaces.widget.SelectBooleanCheckbox) {
	PrimeFaces.widget.SelectBooleanCheckbox = PrimeFaces.widget.SelectBooleanCheckbox.extend({
		init: function(cfg) {
			this._super(cfg);

			if (!this.disabled) {
				var $box = this.box;

				$("label[for='" + this.id + "']").on("mouseenter mouseleave", function(e) {
					$box.toggleClass("ui-state-hover", e.type == "mouseenter");
				}).on("click", function() {
					$box.trigger("click");
				});
			}
		}
	});
}