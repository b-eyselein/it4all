let editor;

function initEditor() {
    const langTools = ace.require('ace/ext/language_tools');

    $('#editor').css({'fontSize': '16px'});

    editor = ace.edit('editor');

    // editor.on('change', updateHiddenTextarea);

    editor.setTheme('ace/theme/eclipse');

    editor.getSession().setMode('ace/mode/' + theMode);

    editor.getSession().setTabSize(2);

    editor.getSession().setUseSoftTabs(true);
    editor.getSession().setUseWrapMode(true);

    editor.setOptions({
        minLines: theMinLines,
        maxLines: theMaxLines,
        enableBasicAutocompletion: true,
        enableSnippets: true,
        enableLiveAutocompletion: false
    });


    const rhymeCompleter = {
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
    langTools.addCompleter(rhymeCompleter);
}

$(document).ready(function () {
    initEditor();
    // updateHiddenTextarea();

    if (theUpdatePrev) {
        editor.on('change', updatePreview);
        updatePreview();
    }
});