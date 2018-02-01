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

/**
 * @param {string} language
 * @param {int} minLines
 * @param {int} maxLines
 */
function initEditor(language, minLines, maxLines) {

    setFontSize(16);

    let newEditor = ace.edit('editor');

    newEditor.setTheme('ace/theme/eclipse');

    newEditor.getSession().setMode('ace/mode/' + language);

    newEditor.getSession().setTabSize(2);

    newEditor.getSession().setUseSoftTabs(true);
    newEditor.getSession().setUseWrapMode(true);

    newEditor.setOptions({minLines, maxLines});

    return newEditor;
}
