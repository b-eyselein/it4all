function onWebCorrectionSuccess(correction) {
    $('#correction').html(correction);
    $('#testButton').prop('disabled', false);
}

/**
 *
 * @param jqXHR {{responseText: string, responseJSON: string}}
 */
function onWebCorrectionError(jqXHR) {
    console.error(jqXHR.responseText);
    $('#correction').html('<div class="alert alert-danger">' + jqXHR.responseText + '</div>');
    $('#testButton').prop('disabled', false);
}

function testSol(theUrl, part) {
    let dataToSend = {
        part,
        solution: editor.getValue()
    };

    // console.log(JSON.stringify(dataToSend));

    $.ajax({
        type: 'PUT',
        // dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: theUrl,
        data: JSON.stringify(dataToSend),
        async: true,
        success: onWebCorrectionSuccess,
        error: onWebCorrectionError
    });
}

/**
 * @param {string} escapedHTML
 */
function unescapeHTML(escapedHTML) {
    return escapedHTML.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
}

function updatePreview() {
    const toWrite = unescapeHTML(editor.getValue());

    const theIFrame = document.getElementById('preview').contentWindow.document;
    theIFrame.open();
    theIFrame.write(toWrite);
    theIFrame.close();

}