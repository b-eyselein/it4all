import * as $ from 'jquery';

import * as CodeMirror from 'codemirror';
import {initEditor} from "../editorHelpers";

import 'codemirror/mode/sql/sql';
import {MatchingResult} from "../matches";
import {ExecutionResultsObject, renderExecution, renderMatchingResult} from "./sqlRenderCorrection";

let editor: CodeMirror.Editor;
let testBtn: JQuery;
let showSampleSolBtn: JQuery;
let sampleSolTab: JQuery;

let correction: JQuery;

interface SqlResult {
    solutionSaved: boolean
    success: 'COMPLETE' | 'PARTIALLY' | 'NONE' | 'ERROR'

    results: SqlCorrectionResult
}

interface SqlCorrectionResult {
    message: string

    columnComparisons: MatchingResult<any, any>
    tableComparisons: MatchingResult<any, any>
    joinExpressionComparisons: MatchingResult<any, any>
    whereComparisons: MatchingResult<any, any>

    additionalComparisons: MatchingResult<any, any>[]

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

    if (response.success === 'ERROR') {
        results.push(`
<div class="alert alert-danger">
    <p>Es gab einen Fehler bei der Korrektur:</p>
    <code>${response.results.message}</code>
</div>`.trim());
    } else {

        results.push(
            renderMatchingResult(response.results.columnComparisons),
            renderMatchingResult(response.results.tableComparisons),
            renderMatchingResult(response.results.joinExpressionComparisons),
            renderMatchingResult(response.results.whereComparisons)
        );

        for (const additionalComp of response.results.additionalComparisons) {
            results.push(renderMatchingResult(additionalComp));
        }

        if (response.results.executionResults.success === 'COMPLETE') {
            results.push(`<span class="text-success">Der Vergleich der Ergebnistabellen war erfolgreich.`);
        } else {
            results.push(`<span class="text-danger">Der Vergleich der Ergebnistabellen war nicht erfolgreich.`);
        }

        $('#executionResultsDiv').html(renderExecution(response.results.executionResults));
    }

    let newHtml = results.map(r => `<p>${r}</p>`).join('\n');
    correction.html(newHtml);

    $('#correctionTabBtn').get()[0].click();
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
    $.ajax({
        url: showSampleSolBtn.data('href'),
        dataType: 'json',
        success: (sampleSolutions: string[]) => {
            const renderedSols = sampleSolutions.map(sampleSol => `
<div class="form-group">
    <div class="card">
        <div class="card-body bg-light">
            <pre id="sampleSolPre">${sampleSol}</pre>
        </div>
    </div>
</div>`.trim());

            sampleSolTab.html(renderedSols.join("\n"));
            $('#sampleSolTabBtn').get()[0].click();
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

    sampleSolTab = $('#sampleSolTab');
});