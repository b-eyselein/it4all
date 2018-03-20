function onChangeLanguageSuccess(response) {
    $('#language').val(response.language);
    editor.setValue(response, 1000000);
}

function changeProgLanguage() {
    let url = '';

    $.ajax({
        type: 'GET',
        url,
        data: 'language=' + $('#langSelect').val(),
        async: true,
        success: onChangeLanguageSuccess
    });
}

/**
 *
 * @param result
 * @param {int} result.id
 * @param {boolean} result.correct
 * @param {string} result.evaluated
 * @param {string} result.awaited
 * @param {string} result.gotten
 */
function renderProgResult(result) {
    return `
<div class="panel panel-${result.correct ? 'success' : 'danger'}">
    <div class="panel-heading">${result.id}. Test von <code>${result.evaluated}</code> war ${result.correct ? '' : ' nicht'} erfolgreich.</div>
    <div class="panel-body">
        <p>Erwartet: <code>${result.awaited.length === 0 ? '""' : result.awaited}</code></p>
        <p>Bekommen: <code>${result.gotten.length === 0 ? '""' : result.gotten}</code></p>
    </div>
</div>`.trim();
}

/**
 * @param {object} response
 * @param {boolean} response.solutionSaved
 * @param {object[]} response.results
 */
function onProgCorrectionSuccess(response) {
    console.log(JSON.stringify(response, null, 2));

    let html = `<div class="alert alert-${response.solutionSaved ? 'success' : 'danger'}">Ihre Lösung wurde ${response.solutionSaved ? '' : ' nicht'} gespeichert.</div>`;


    for (let i = 0; i < response.results.length; i = i + 3) {
        let firstResult = response.results[i] || null;
        let secondResult = response.results[i + 1] || null;
        let thirdNextResult = response.results[i + 2] || null;

        html += `
<div class="row">
    <div class="col-md-4">${firstResult != null ? renderProgResult(firstResult) : ''}</div>
    <div class="col-md-4">${secondResult != null ? renderProgResult(secondResult) : '' }</div>
    <div class="col-md-4">${thirdNextResult != null ? renderProgResult(thirdNextResult) : ''}</div>
</div>`.trim();

    }
    $('#correction').html(html);
}

function onProgCorrectionError(jqXHR) {
    console.error(jqXHR.responseText);
}

function testSol() {
    let exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.ExerciseController.correctLive('programming', exerciseId, exercisePart).url;

    $('#correction').html('');


    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url,
        data: JSON.stringify({
            solution: {
                language: $('#langSelect').val(),
                implementation: editor.getValue()
            }
        }),
        async: true,
        success: onProgCorrectionSuccess,
        error: onProgCorrectionError
    });

}

$(document).ready(function () {
    $('#testBtn').click(testSol);
});