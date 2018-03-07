/**
 * @param {object} matched
 * @param {string} matched.success
 * @param {string} matched.userArg
 * @param {string} matched.sampleArg
 */
function displayMatch(matched) {
    let divClass, spanClass;
    if (matched.success === 'SUCCESSFUL_MATCH') {
        divClass = 'success';
        spanClass = 'ok';
    } else if (matched.success === 'PARTIAL_MATCH') {
        divClass = 'warning';
        spanClass = 'question-sign';
    } else {
        divClass = 'danger';
        spanClass = 'remove';
    }

    return `
<div class="alert alert-${divClass}">
    <div class="row">
        <div class="col-sm-2"><span class="glyphicon glyphicon-${spanClass}"></span></div>
        <div class="col-sm-4">Nutzer: <code>${matched.userArg}</code></div>
        <div class="col-sm-4">Muster: <code>${matched.sampleArg}</code></div>
    </div>
</div>`
}

/**
 * @param {object} matchObj
 * @param {boolean} matchObj.success
 *
 * @param {object[]} matchObj.matched
 *
 * @param {string[]} matchObj.only_user
 * @param {string[]} matchObj.only_sample
 *
 * @param {string} matchName
 *
 * @return {string}
 */
function renderMatchingResult(matchObj, matchName) {
    if (matchObj.success)
        return `<div class="alert alert-success"><span class="glyphicon glyphicon-ok"></span> Der Vergleich der ${matchName} war erfolgreich.</div>`;

    return `
<div class="panel panel-danger">
    <div class="panel-heading"><b>Der Vergleich der ${matchName} war nicht komplett korrekt:</b></div>
    <div class="panel-body">
        <div class="row">
            <div class="col-md-6">
                <p><b>(Teils) passende ${matchName}:</b></p>
                ${matchObj.matched.map(displayMatch).join('\n')}
            </div>
            
            <div class="col-md-3">
                <p><b>Falsche ${matchName}:</b></p>
                ${matchObj.only_user.map((c) => `<div class="alert alert-danger"><span class="glyphicon glyphicon-remove"></span> <code>${c}</code></div>`).join('\n')}
            </div>
            
            <div class="col-md-3">
                <p><b>Fehlende ${matchName}:</b></p>
                ${matchObj.only_sample.map((c) => `<div class="alert alert-warning"><span class="glyphicon glyphicon-question-sign"></span> <code>${c}</code></div>`).join('\n')}
            </div>
        </div>
    </div>
</div>`.trim();
}

/**
 * @param {{colNames: string[], content: Array.<Array.<{content: string, different: boolean}>>}} table
 */
function renderTable(table) {
    let rendered = `
<table class="table table-striped table-bordered">
    <thead>
        <tr>
            ${table.colNames.map((cn) => `<td>${cn}</td>`).join('')}
        </tr>
    </thead>
    <tbody>`.trim();

    for (let row of table.content) {
        rendered += `<tr>`;
        for (let cell of row) {
            rendered += `<td${cell.different ? ` class="danger"` : ''}>${cell.content}</td>`;
        }
        rendered += `</tr>`;
    }
    rendered += `</tbody>
</table>`;

    return rendered;
}

/**
 * @param {object} execution
 * @param {string} execution.success
 * @param {{colNames: string[], content: Array.<Array.<{content: string, different: boolean}>>}} execution.user
 * @param {{colNames: string[], content: Array.<Array.<{content: string, different: boolean}>>}} execution.sample
 *
 * @return {string}
 */
function renderExecution(execution) {
    return `
<div class="panel panel-default">
    <div class="panel-heading">Vergleich der Ergebnistabellen</div>
    <div class="panel-body">
        <div class="row">
            <div class="col-md-6">
                <p><b>Nutzer</b></p>
                ${execution.user != null ? renderTable(execution.user) : `<div class="alert alert-danger">Es gab einen Fehler beim Ausführen Ihrer Lösung!</div>`}
            </div>
            <div class="col-md-6">
                <p><b>Muster</b></p>
                ${execution.sample != null ? renderTable(execution.sample) : `<div class="alert alert-danger">Es gab einen Fehler beim Ausführen der Musterlösung!</div>`}
            </div>
        </div>
    </div>
</div>`.trim();
}

/**
 * @param {object} response
 *
 * @param {object} response.columns
 * @param {object} response.tables
 * @param {object} response.wheres
 * @param {object} response.groupBy
 * @param {object} response.orderBy
 *
 * @param {object} response.execution
 */
function onSqlCorrectionSuccess(response) {
    $('#testBtn').prop('disabled', false);

    let newHtml = '';

    newHtml += renderMatchingResult(response.columns, "Spalten");
    newHtml += renderMatchingResult(response.tables, "Tabellen");
    newHtml += renderMatchingResult(response.wheres, "Bedingungen");

    if (response.groupBy != null)
        newHtml += renderMatchingResult(response.groupBy, "Group Bys");

    if (response.orderBy != null)
        newHtml += renderMatchingResult(response.orderBy, "Order Bys");

    newHtml += renderExecution(response.execution);

    $('#newCorrectionDiv').html(newHtml);
}

/**
 * @param {object} jqXHR
 * @param {object} jqXHR.responseJSON
 * @param {string} jqXHR.responseJSON.msg
 */
function onSqlCorrectionError(jqXHR) {
    $('#testBtn').prop('disabled', false);

    $('#newCorrectionDiv').html(`
<div class="panel panel-danger">
    <div class="panel-heading"><b>Es gab einen Fehler bei der Korrektur:</b></div>
    <div class="panel-body">
        <pre>${jqXHR.responseJSON.msg}</pre>
    </div>
</div>`.trim());

}

function testSqlSol() {
    let collId = $('#collId').val(), exerciseId = $('#exerciseId').val();
    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.exes.CollectionController.correctLive('sql', collId, exerciseId).url;

    let learnerSolution = editor.getValue();
    if (learnerSolution === "") {
        $('#newCorrectionDiv').html(`<div class="alert alert-danger">Sie können keine leere Query abgeben!</div>`);
        return;
    }

    $('#testBtn').prop('disabled', true);

    $.ajax({
        type: 'PUT',
        url,
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify({learnerSolution}),
        async: true,
        success: onSqlCorrectionSuccess,
        error: onSqlCorrectionError
    });
}

$(document).ready(function () {
    $('#testBtn').click(testSqlSol);
});