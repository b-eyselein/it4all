/**
 * @param {html} response
 */
function onSqlCorrectionSuccess(response) {
    $('#testButton').prop('disabled', false);
    $('#correctionDiv').html(response);
}

/**
 * @param {object} jqXHR
 * @param {string} jqXHR.responseText
 * @param {string} textStatus
 * @param {string} errorThrown
 */
function onSqlCorrectionError(jqXHR, textStatus, errorThrown) {
    console.error(jqXHR.responseText);
    $('#testButton').prop('disabled', false);
}

function testSqlSol(theUrl) {
    let toSend = editor.getValue();
    if (toSend === "") {
        return;
    }

    $('#testButton').prop('disabled', true);

    $.ajax({
        type: 'PUT',
        url: theUrl,
        dataType: 'text',
        contentType: 'application/json',
        data: JSON.stringify({
            learnerSolution: toSend
        }),
        async: true,
        success: onSqlCorrectionSuccess,
        error: onSqlCorrectionError
    });
}

function updatePreview() {
    // Do nothing...
}