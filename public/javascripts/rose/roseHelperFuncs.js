let editor;

/**
 * @param {int} value
 */
function setFontSize(value) {
    $('#editor').css({'fontSize': value + 'px'})
}

function changeFontSize(value) {
    const fontSizeElement = $('#fontSize');
    const fontSize = parseInt(fontSizeElement.text()) + value;
    setFontSize(fontSize);
    fontSizeElement.text(fontSize);
}

function initEditor() {
    const langTools = ace.require('ace/ext/language_tools');

    setFontSize(16);

    editor = ace.edit('editor');

    editor.setTheme('ace/theme/eclipse');

    editor.getSession().setMode('ace/mode/python');

    editor.getSession().setTabSize(2);

    editor.getSession().setUseSoftTabs(true);
    editor.getSession().setUseWrapMode(true);

    editor.setOptions({
        minLines: 20,
        maxLines: 30,
        enableBasicAutocompletion: true,
        enableSnippets: true,
        enableLiveAutocompletion: false
    });

    // noinspection JSUnusedGlobalSymbols
    const robotCompleter = {
        getCompletions: function (editor, session, pos, prefix, callback) {
            let selfFunctions = ['self.go_up()', 'self.go_down()', 'self.go_left()', 'self.go_right()'];

            callback(null, selfFunctions.map(function (word) {
                return {
                    caption: word,
                    value: word,
                    meta: "self"
                };
            }));
        }
    };
    langTools.addCompleter(robotCompleter);
}

$(document).ready(function () {
    initEditor();
});