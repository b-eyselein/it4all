function onChangeLanguageSuccess(response) {
    $('#language').val(response.language);
    editor.setValue(response, 1000000);
}

function changeProgLanguage() {
    // @controllers.routes.ExerciseController.progGetDeclaration("")

    let url = '';

    $.ajax({
        type: 'GET',
        url,
        data: 'language=' + $('#langSelect').val(),
        async: true,
        success: onChangeLanguageSuccess
    });
}

function printValue(value) {
    if (value == null) {
        return 'null';
    } else if (value.length === 0) {
        return '""';
    } else {
        return value;
    }
}

/**
 * @param {object} result
 * @param {int} result.id
 * @param {boolean} result.correct
 * @param {object} result.input
 * @param {string} result.awaited
 * @param {string} result.gotten
 * @param {string} result.consoleOutput
 */
function renderProgResult(result) {
    let consoleOut = '';
    if (result.consoleOutput !== null) {
        consoleOut = '<p>Konsolenausgabe: <pre>' + result.consoleOutput + '</pre></p>';
    }

    return `
<div class="panel panel-${result.correct ? 'success' : 'danger'}">
    <div class="panel-heading">${result.id}. Test war ${result.correct ? '' : ' nicht'} erfolgreich.</div>
    <div class="panel-body">
        <p>Eingabe: <pre>${result.input.toString()}</pre></p>
        <p>Erwartet: <code>${printValue(result.awaited)}</code></p>
        <p>Bekommen: <code>${printValue(result.gotten)}</code></p>
        ${consoleOut}
    </div>
</div>`.trim();
}

/**
 * @param {object} response
 * @param {boolean} response.solutionSaved
 * @param {object[]} response.results
 */
function onProgCorrectionSuccess(response) {
    console.warn(JSON.stringify(response, null, 2));

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