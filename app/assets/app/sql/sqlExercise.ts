import * as $ from 'jquery';

import * as CodeMirror from 'codemirror';
import {initEditor} from "../editorHelpers";

import 'codemirror/mode/sql/sql';

let editor: CodeMirror.Editor;
let testBtn: JQuery;

function displayMatchResult(matchingRes: Match, matchName: string) {
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

    let mainExplanation = explainMatch(matchingRes, matchName);

    return `
<div class="col-md-6">
    <div class="alert alert-${alertClass}">
        <p><span class="glyphicon glyphicon-${glyphicon}"></span> ${mainExplanation}</p>
    </div>
</div>`.trim();
}

interface Matches {
    success: boolean
    matches: Match[],
}

function renderMatchingResult(matchObj: Matches, matchName: string, matchSingularName: string): string {
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
        ${matchObj.matches.map(s => displayMatchResult(s, matchSingularName)).join('\n')}
    </div>
</div>`.trim();
    }
}

interface ExecTableCell {
    content: string
    different: boolean
}

interface ExecutionTable {
    colNames: string[]
    content: ExecTableCell[][]
}

function renderExecutionResult(executionTable: ExecutionTable): string {
    if (executionTable == null) {
        return `<div class="alert alert-danger">Es gab einen Fehler beim Ausführen Ihrer Lösung!</div>`;
    } else {
        let rendered = `
<table class="table table-striped table-bordered">
    <thead>
        <tr>
            ${executionTable.colNames.map((cn) => `<td>${cn}</td>`).join('')}
        </tr>
    </thead>
    <tbody>`.trim();

        for (let row of executionTable.content) {
            rendered += `<tr>${row.map((cell) => `<td${cell.different ? ` class="danger"` : ''}>${cell.content}</td>`).join('')}</tr>`;
        }

        rendered += `</tbody>
</table>`;

        return rendered;
    }
}

interface ExecutionResultsObject {
    success: string
    userResult: ExecutionTable | null
    sampleResult: ExecutionTable | null
}

function renderExecution(executionResults: ExecutionResultsObject): string {
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

interface Match {
    success: 'SUCCESSFUL_MATCH' | 'PARTIAL_MATCH' | 'UNSUCCESSFUL_MATCH' | 'ONLY_SAMPLE' | 'ONLY_USER'
    userArg: string | null
    sampleArg: string | null
}


function explainMatch(aMatch: Match, matchName: string): string {
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

interface SqlCorrectionResult {
    columns: Matches
    tables: Matches
    wheres: Matches
    groupBy: Matches | null
    orderBy: Matches | null
    executionResults: ExecutionResultsObject
}

function onSqlCorrectionSuccess(response: SqlCorrectionResult): void {
    const correctionDiv = $('#newCorrectionDiv');
    correctionDiv.html('');

    testBtn.prop('disabled', false);

    let newHtml: string = '';

    newHtml += renderMatchingResult(response.columns, "Spalten", "der Spalte");
    newHtml += renderMatchingResult(response.tables, "Tabellen", "der Tabelle");
    newHtml += renderMatchingResult(response.wheres, "Bedingungen", "der Bedingung");

    if (response.groupBy != null) {
        newHtml += renderMatchingResult(response.groupBy, "Group Bys", "des Group By-Statement");
    }

    if (response.orderBy != null) {
        newHtml += renderMatchingResult(response.orderBy, "Order Bys", "des Order By-Statement");
    }

    newHtml += renderExecution(response.executionResults);

    correctionDiv.html(newHtml);
}

function onSqlCorrectionError(jqXHR): void {
    testBtn.prop('disabled', false);

    $('#newCorrectionDiv').html(`
<div class="panel panel-danger">
    <div class="panel-heading"><b>Es gab einen Fehler bei der Korrektur:</b></div>
    <div class="panel-body">
        <pre>${jqXHR.responseJSON.msg}</pre>
    </div>
</div>`.trim());

}

function testSqlSol(): void {

    let learnerSolution: string = editor.getValue();
    if (learnerSolution === "") {
        $('#newCorrectionDiv').html(`<div class="alert alert-danger">Sie können keine leere Query abgeben!</div>`);
        return;
    }

    testBtn.prop('disabled', true);

    $.ajax({
        type: 'PUT',
        url: testBtn.data('url'),
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify({learnerSolution}),
        async: true,
        success: onSqlCorrectionSuccess,
        error: onSqlCorrectionError
    });
}

$(() => {
    editor = initEditor('text/x-mysql');

    testBtn = $('#testBtn');
    testBtn.on('click', testSqlSol);
});