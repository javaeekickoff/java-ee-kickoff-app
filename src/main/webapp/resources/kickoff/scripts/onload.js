/*
 * Put all inline scripts here which should be immediately executed during window load.
 * 
 * IMPORTANT: Don't define functions or document event listeners here! Use functions.js and listeners.js for that.
 */

/**
 * Initialize script error logging.
 */
window.onerror = function(message, source, line, column, error) {
	try {
		$.post("/script-error", $.param({
			url: window.location.href,
			message: message,
			source: source,
			line: line,
			column: column,
			error: error ? error.stack : null
		}));
	}
	catch(e) {
		// Ignore.
	}
};