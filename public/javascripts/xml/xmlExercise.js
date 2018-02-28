/**
 * @param {object} response
 * @param {boolean} response.solSaved
 * @param {boolean} response.success
 * @param {object[]} response.results
 * @param {string} response.results.errorType
 * @param {string} response.results.errorMessage
 * @param {int} response.results.line
 * @param {string} response.results.success
 */
function onXmlDocumentCorrectionSuccess(response) {
    let html = '';

    if (response.solSaved) {
        html += `<div class="alert alert-success">Ihre Lösung wurde gespeichert.</div>`;
    } else {
        html += `<div class="alert alert-danger">Ihre Lösung konnte nicht gespeichert werden!</div>`;
    }

    if (response.success) {
        html += `<div class="alert alert-success">Die Korrektur war erfolgreich. Es wurden keine Fehler gefunden.</div>`;
    } else {
        for (let xmlError of response.results) {
            let cls = xmlError.errorType === 'WARNING' ? 'warning' : 'danger';
            html += `<div class="alert alert-${cls}"><b>Fehler in Zeile ${xmlError.line}</b>: ${xmlError.errorMessage}</div>`;
        }
    }

    $('#correction').html(html);
}


function onXmlGrammarCorrectionSuccess(response) {
    console.log(response);
}

/**
 * @param {object} jqXHR
 * @param {object} jqXHR.responseJSON
 * @param {string} jqXHR.responseJSON.msg
 */
function onXmlCorrectionError(jqXHR) {
    $('#correctionDiv').html(`<div class="alert alert-danger">${jqXHR.responseJSON.msg}</div>`);
}


function testSol() {
    let exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();
    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.exes.ExerciseController.correctLive('xml', exerciseId, exercisePart).url;

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url,
        data: JSON.stringify({solution: editor.getValue()}),
        async: true,
        success: (exercisePart === 'document') ? onXmlDocumentCorrectionSuccess : onXmlGrammarCorrectionSuccess,
        error: onXmlCorrectionError
    });
}

function updatePreview() {
    // Do nothing...
}

$(document).ready(function () {
    $('#testButton').click(testSol);
});