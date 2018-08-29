import * as $ from 'jquery';

import * as CodeMirror from 'codemirror';
import {initEditor} from "../editorHelpers";

import 'codemirror/mode/sql/sql';
import {AnalysisResult, Match, MatchingResult} from "../matches";

let editor: CodeMirror.Editor;
let testBtn: JQuery;

let correction: JQuery;

interface SqlResult {
    solutionSaved: boolean
    success: 'COMPLETE' | 'PARTIALLY' | 'NONE' | 'ERROR'

    results: SqlCorrectionResult
}

interface SqlCorrectionResult {
    message: string

    columns: MatchingResult<any, any>
    tables: MatchingResult<any, any>
    wheres: MatchingResult<any, any>
    groupBy: MatchingResult<any, any> | null
    orderBy: MatchingResult<any, any> | null
    insertedValues: MatchingResult<any, any> | null
    executionResults: ExecutionResultsObject
}

interface ExecutionResultsObject {
    success: string
    userResult: ExecutionTable | null
    sampleResult: ExecutionTable | null
}


interface ExecTableCell {
    content: string
    different: boolean
}

interface ExecutionTable {
    colNames: string[]
    content: ExecTableCell[][]
}

function displayMatchResult(matchingRes: Match<any, any>, matchName: string) {
    let alertClass, glyphicon;

    switch (matchingRes.matchType) {
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

    return `<span class="text-${alertClass}">${mainExplanation}</span>`;
}


function renderMatchingResult(matchObj: MatchingResult<string, AnalysisResult>, matchName: string, matchSingularName: string): string {
    if (matchObj.success) {
        return `<span class="text-success">Der Vergleich der ${matchName} war erfolgreich.</span>`;
    } else {
        return `
<span class="text-danger">Der Vergleich der ${matchName} war nicht komplett korrekt:</span>
<ul>
    ${matchObj.matches.map(s => `<li>${displayMatchResult(s, matchSingularName)}</li>`).join('\n')}
</ul>`.trim();
    }
}

function renderExecutionResult(executionTable: ExecutionTable): string {
    if (executionTable == null) {
        return `<div class="alert alert-danger">Es gab einen Fehler beim Ausführen Ihrer Lösung!</div>`;
    } else {
        let tableBody: string = '';
        for (let row of executionTable.content) {
            tableBody += `<tr>${row.map((cell) => `<td${cell.different ? ` class="danger"` : ''}>${cell.content}</td>`).join('')}</tr>`;
        }

        return `
<table class="table table-striped table-bordered">
    <thead>
        <tr>${executionTable.colNames.map((cn) => `<td>${cn}</td>`).join('')}</tr>
    </thead>
    <tbody>${tableBody}</tbody>
</table>`;
    }
}

function renderExecution(executionResults: ExecutionResultsObject): string {
    return `
<h3 class="text-center">Vergleich der Ergebnistabellen</h3>
<div class="row">
    <div class="col-sm-6">
        <h4 class="text-center">Nutzer</h4>
        ${renderExecutionResult(executionResults.userResult)}
    </div>
    <div class="col-sm-6">
        <h4 class="text-center">Muster</h4>
        ${renderExecutionResult(executionResults.sampleResult)}
    </div>
</div>`.trim();
}

function explainMatch(aMatch: Match<any, any>, matchName: string): string {
    switch (aMatch.matchType) {
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

function onSqlCorrectionSuccess(response: SqlResult): void {
    testBtn.prop('disabled', false);

    console.warn(JSON.stringify(response, null, 2));


    correction.html('');

    let results: string[] = [];

    if (response.solutionSaved) {
        results.push(`<span class="text-success">Ihre Lösung wurde gespeichert.</span>`);
    } else {
        results.push(`<span class=text-danger>Ihre Lösung konnte nicht gespeichert werden!</span>`);
    }


    switch (response.success) {
        case 'ERROR':
            results.push(`
<div class="alert alert-danger">
    <p>Es gab einen Fehler bei der Korrektur:</p>
    <code>${response.results.message}</code>
</div>`.trim());
            break;

        default:
            results.push(
                renderMatchingResult(response.results.columns, "Spalten", "der Spalte"),
                renderMatchingResult(response.results.tables, "Tabellen", "der Tabelle"),
                renderMatchingResult(response.results.wheres, "Bedingungen", "der Bedingung")
            );

            if (response.results.groupBy != null) {
                results.push(renderMatchingResult(response.results.groupBy, "Group Bys", "des Group By-Statement"));
            }

            if (response.results.orderBy != null) {
                results.push(renderMatchingResult(response.results.orderBy, "Order Bys", "des Order By-Statement"));
            }

            if (response.results.insertedValues != null) {
                results.push(renderMatchingResult(response.results.insertedValues, "hinzugefügten Werte", "der Werte"));
            }

            $('#executionResultsDiv').html(renderExecution(response.results.executionResults));
            break;
    }

    let newHtml = results.map(r => `<p>${r}</p>`).join('\n');
    correction.html(newHtml);
}

function onSqlCorrectionError(jqXHR): void {
    testBtn.prop('disabled', false);

    correction.html(`
<div class="panel panel-danger">
    <div class="panel-heading"><b>Es gab einen Fehler bei der Korrektur:</b></div>
    <div class="panel-body">
        <pre>${jqXHR.responseJSON.msg}</pre>
    </div>
</div>`.trim());

}

function testSqlSol(): void {
    $('#correctionDiv').prop('hidden', false);

    let learnerSolution: string = editor.getValue();
    if (learnerSolution === "") {
        $('#correction').html(`<div class="alert alert-danger">Sie können keine leere Query abgeben!</div>`);
        return;
    }

    testBtn.prop('disabled', true);

    $.ajax({
        type: 'PUT',
        url: testBtn.data('url'),
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify(learnerSolution),
        async: true,
        success: onSqlCorrectionSuccess,
        error: onSqlCorrectionError
    });
}

$(() => {
    editor = initEditor('text/x-mysql', 'sqlEditor');

    testBtn = $('#testBtn');
    testBtn.on('click', testSqlSol);

    correction = $('#correction');
});