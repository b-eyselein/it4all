import * as $ from 'jquery';

import 'codemirror/mode/sql/sql';
import * as CodeMirror from 'codemirror';
import {initEditor} from "../editorHelpers";

import {focusOnCorrection, testTextExerciseSolution} from '../textExercise';
import {displayStringSampleSolution, domReady, initShowSampleSolBtn, StringSampleSolution} from "../otherHelpers";

import {MatchingResult} from "../matches";
import {ExecutionResultsObject, renderExecution, renderMatchingResult} from "./sqlRenderCorrection";

let editor: CodeMirror.Editor;
let testBtn: HTMLButtonElement;
let correctionDiv: HTMLDivElement;

interface SqlResult {
    solutionSaved: boolean
    success: 'COMPLETE' | 'PARTIALLY' | 'NONE' | 'ERROR'
    points: number
    maxPoints: number
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
    testBtn.disabled = false;

    correctionDiv.innerHTML = '';

    let results: string[] = [];

    if (response.solutionSaved) {
        results.push(`<span class="text-success">Ihre Lösung wurde gespeichert.</span>`);
    } else {
        results.push(`<span class=text-danger>Ihre Lösung konnte nicht gespeichert werden!</span>`);
    }

    results.push(`<p>Sie haben ${response.points} von ${response.maxPoints} erreicht.</p>`.trim());

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

        document.querySelector<HTMLDivElement>('#executionResultsDiv').innerHTML =
            renderExecution(response.results.executionResults);
    }

    const newHtml: string = results.map(r => `<p>${r}</p>`).join('\n');
    correctionDiv.innerHTML = newHtml;

    focusOnCorrection();
}

function testSqlSol(): void {
    document.querySelector<HTMLDivElement>('#correctionDiv').hidden = false;

    let learnerSolution: string = editor.getValue();

    if (learnerSolution === "") {
        alert("Sie können keine leere Query abgeben!");
        return;
    }

    testBtn.disabled = true;

    testTextExerciseSolution<string, SqlCorrectionResult>(testBtn, learnerSolution, onSqlCorrectionSuccess);
}

domReady(() => {
    editor = initEditor('text/x-mysql', 'textEditor');

    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSqlSol;

    correctionDiv = document.querySelector<HTMLDivElement>('#correction');

    initShowSampleSolBtn<StringSampleSolution[]>(sqlSamples =>
        sqlSamples.map(displayStringSampleSolution).join('\n')
    );

});
