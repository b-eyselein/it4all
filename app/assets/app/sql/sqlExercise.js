/**
 *
 * @param {object} matchingRes
 * @param {string} matchingRes.success
 * @param {string[]} matchingRes.explanations
 * @param {function} explanationFunc
 *
 * @return {string}
 */
function displayMatchResult(matchingRes, explanationFunc) {
    let alertClass, glyphicon;

    switch (matchingRes.success) {
        case 'SUCCESSFUL_MATCH':
            alertClass = 'success';
            glyphicon = 'ok';
            break;
        case 'PARTIAL_MATCH':
            alertClass = 'warning';
            glyphicon = 'question-sign';
            break;
        case 'UNSUCCESSFUL_MATCH':
            alertClass = 'danger';
            glyphicon = 'remove';
            break;
        case 'ONLY_USER':
            alertClass = 'danger';
            glyphicon = 'remove';
            break;
        case 'ONLY_SAMPLE':
            alertClass = 'danger';
            glyphicon = 'remove';
            break;
        default:
            alertClass = 'info';
            glyphicon = 'remove';
    }

    let mainExplanation = explanationFunc(matchingRes);

    return `
<div class="col-md-6">
    <div class="alert alert-${alertClass}">
        <p><span class="glyphicon glyphicon-${glyphicon}"></span> ${mainExplanation}</p>
    </div>
</div>`.trim();
}

/**
 * @param {object} matchObj
 * @param {boolean} matchObj.success
 * @param {object[]} matchObj.matches
 * @param {string} matchName
 * @param {function} explanationFunc
 * @return {string}
 */
function renderMatchingResult(matchObj, matchName, explanationFunc) {
    if (matchObj.success) {
        return `
<div class="alert alert-success">
    <span class="glyphicon glyphicon-ok"></span> Der Vergleich der ${matchName} war erfolgreich.
</div>`.trim();
    } else {
        return `
<div class="panel panel-danger">
    <div class="panel-heading"><b>Der Vergleich der ${matchName} war nicht komplett korrekt:</b></div>
    <div class="panel-body">
        ${matchObj.matches.map(s => displayMatchResult(s, explanationFunc)).join('\n')}
    </div>
</div>`.trim();
    }
}

/**
 * @param {object} table
 * @param {string[]} table.colNames
 * @param {Array.<Array.<{content: string, different: boolean}>>} table.content
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
 * @param {object} execResult
 *
 * @return {string}
 */
function renderExecutionResult(execResult) {
    if (execResult == null) {
        return `<div class="alert alert-danger">Es gab einen Fehler beim Ausführen Ihrer Lösung!</div>`;
    } else {
        return renderTable(execResult);
    }
}

/**
 * @param {object} executionResults
 * @param {string} executionResults.success
 * @param {object} executionResults.userResult
 * @param {object} executionResults.sampleResult
 *
 * @return {string}
 */
function renderExecution(executionResults) {
    return `
<div class="panel panel-default">
    <div class="panel-heading">Vergleich der Ergebnistabellen</div>
    <div class="panel-body">
        <div class="row">
            <div class="col-md-6">
                <p><b>Nutzer</b></p>
                ${renderExecutionResult(executionResults.userResult)}
            </div>
            <div class="col-md-6">
                <p><b>Muster</b></p>
                ${renderExecutionResult(executionResults.sampleResult)}
            </div>
        </div>
    </div>
</div>`.trim();
}

/**
 * @param {object} aMatch
 * @param {string} aMatch.success
 * @param {string|null} aMatch.userArg
 * @param {string|null} aMatch.sampleArg
 * @param {string[]} aMatch.explanations
 *
 * @param {string} matchName
 *
 * @return {string}
 */
function explainMatch(aMatch, matchName) {
    switch (aMatch.success) {
        case 'SUCCESSFUL_MATCH':
            return `Die Angabe ${matchName} <code>${aMatch.userArg}</code> war richtig.`;
        case 'PARTIAL_MATCH':
        case 'UNSUCCESSFUL_MATCH':
            return `Die Angabe ${matchName} <code>${aMatch.userArg}</code> war nicht komplett richtig. Erwartet wurde <code>${aMatch.sampleArg}</code>.`;
        case 'ONLY_SAMPLE':
            return `Die Angabe ${matchName} <code>${aMatch.sampleArg}</code> fehlt!`;
        case 'ONLY_USER':
            return `Die Angabe ${matchName} <code>${aMatch.userArg}</code> ist falsch!`;
        default:
            return 'Es gab einen Fehler!';
    }
}


/**
 * @param {object} response
 *
 * @param {object} response.columns
 * @param {object} response.tables
 * @param {object} response.wheres
 * @param {object} response.groupBy
 * @param {object} response.orderBy
 * @param {object} response.executionResults
 */
function onSqlCorrectionSuccess(response) {
    console.warn(JSON.stringify(response, null, 2));

    $('#testBtn').prop('disabled', false);

    let newHtml = '';

    newHtml += renderMatchingResult(response.columns, "Spalten", m => explainMatch(m, "der Spalte"));
    newHtml += renderMatchingResult(response.tables, "Tabellen", m => explainMatch(m, "der Tabelle"));
    newHtml += renderMatchingResult(response.wheres, "Bedingungen", m => explainMatch(m, "der Bedingung"));

    if (response.groupBy != null) {
        newHtml += renderMatchingResult(response.groupBy, "Group Bys", m => explainMatch(m, "des Group By-Statement"));
    }

    if (response.orderBy != null) {
        newHtml += renderMatchingResult(response.orderBy, "Order Bys", m => explainMatch(m, "des Order By-Statement"));
    }

    newHtml += renderExecution(response.executionResults);

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
    let url = jsRoutes.controllers.CollectionController.correctLive('sql', collId, exerciseId).url;

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