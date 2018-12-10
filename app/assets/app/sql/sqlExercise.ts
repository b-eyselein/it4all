import * as $ from 'jquery';

import * as CodeMirror from 'codemirror';
import {initEditor} from "../editorHelpers";

import 'codemirror/mode/sql/sql';
import {MatchingResult} from "../matches";
import {ExecutionResultsObject, renderExecution, renderMatchingResult} from "./sqlRenderCorrection";

let editor: CodeMirror.Editor;
let testBtn: JQuery;
let showSampleSolBtn: JQuery;
let sampleSolPre: JQuery;

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

function onSqlCorrectionSuccess(response: SqlResult): void {
    testBtn.prop('disabled', false);

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
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token);
        },
        success: onSqlCorrectionSuccess,
        error: onSqlCorrectionError
    });
}

function onShowSampleSol(): void {
    console.error('showing sample sol!');

    $.ajax({
        url: showSampleSolBtn.data('href'),
        success: (sampleSol: string) => {
            // console.warn(sampleSol);
            sampleSolPre.parent().parent().prop('hidden', false);
            sampleSolPre.html(sampleSol);
        }
    })
}

$(() => {
    editor = initEditor('text/x-mysql', 'sqlEditor');

    testBtn = $('#testBtn');
    testBtn.on('click', testSqlSol);

    correction = $('#correction');

    showSampleSolBtn = $('#showSampleSolBtn');
    showSampleSolBtn.on('click', onShowSampleSol);

    sampleSolPre = $('#sampleSolPre');
});