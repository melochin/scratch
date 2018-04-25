const Remarkable = require('remarkable');

// This values are default
const markdown = new Remarkable({
    html:         false,        // Enable html tags in source
    xhtmlOut:     false,        // Use '/' to close single tags (<br />)
    breaks:       false,        // Convert '\n' in paragraphs into <br>
    langPrefix:   'language-',  // CSS language prefix for fenced blocks
    linkify:      false,        // Autoconvert url-like texts to links
    typographer:  false,        // Enable smartypants and other sweet transforms
    // Highlighter function. Should return escaped html,
    // or '' if input not changed
    highlight: function (/*str, , lang*/) { return ''; }
});

module.exports.markdown = markdown;