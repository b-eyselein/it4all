function changeFontsize(value) {
    const fontsizeElement = document.getElementById('fontsize');
    const fontsize = parseInt(fontsizeElement.innerHTML) + value;
    document.getElementById('editor').style.fontSize = fontsize + 'px';
    fontsizeElement.innerHTML = fontsize;
}

function initEditor() {
    document.getElementById('editor').style.fontSize = '16px';
    editor = ace.edit('editor');

    editor.on('change', updateHiddenTextarea);

    editor.setTheme('ace/theme/eclipse');
    editor.getSession().setMode('ace/mode/' + theMode);
    editor.getSession().setTabSize(2);
    editor.getSession().setUseSoftTabs(true);
    editor.getSession().setUseWrapMode(true);
    editor.setOptions({
        minLines: theMinLines,
        maxLines: theMaxLines
    });
}

function updateHiddenTextarea() {
    $('#learnerSolution').val(editor.getValue());
}

function toParam(input) {
    return input.id + '=' + encodeURIComponent(input.value);
}

function paramFilter(input, element) {
    return element.id && element.value;
}

function extractParameters() {
    const inputs = $('form input, form textarea').filter(paramFilter);
    return $.map(inputs, toParam).join('&');
}

function testTheSolution(theUrl) {
    $('#testButton').prop('disabled', true);
    $.ajax({
        type: 'PUT',
        url: theUrl,
        data: extractParameters(),
        // FIXME: dataType: 'json' ?,
        async: true,
        success: onSuccess,
        error: onError
    });
}

function onSuccess(correction) {
    $('#correction').html(correction);
    $('#testButton').prop('disabled', false);
}

/**
 *
 * @param jqXHR {{responseText: string, responseJSON: string}}
 */
function onError(jqXHR) {
    $('#correction').html('<div class=\'alert alert-danger\'>' + jqXHR.responseJSON + '</div>');
    $('#testButton').prop('disabled', false);
}

function updatePreview() {
    const toWrite = unescapeHTML(editor.getValue());

    const theIFrame = document.getElementById('preview').contentWindow.document;
    theIFrame.open();
    theIFrame.write(toWrite);
    theIFrame.close();
}

function unescapeHTML(escapedHTML) {
    return escapedHTML.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
}

$(document).ready(function () {
    initEditor();
    updateHiddenTextarea();

    if (theUpdatePrev) {
        editor.on('change', updatePreview);
        updatePreview();
    }
});
