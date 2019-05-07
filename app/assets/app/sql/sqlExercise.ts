import * as CodeMirror from 'codemirror';
import 'codemirror/mode/sql/sql';
import {initEditor} from "../editorHelpers";

import {
    displayStringSampleSolution,
    domReady,
    focusOnCorrection,
    initShowSampleSolBtn,
    StringSampleSolution,
    testExerciseSolution
} from "../otherHelpers";

import {MatchingResult} from "../matches";
import {ExecutionResultsObject, renderExecution, renderMatchingResult} from "./sqlRenderCorrection";

let solutionChanged: boolean = false;

let editor: CodeMirror.Editor;
let testBtn: HTMLButtonElement;

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

    solutionChanged = false;

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
    document.querySelector<HTMLDivElement>('#correction').innerHTML = newHtml;

    focusOnCorrection();
}

function testSol(): void {
    let learnerSolution: string = editor.getValue();

    if (learnerSolution.length === 0) {
        alert("Sie können keine leere Query abgeben!");
        return;
    }

    testExerciseSolution<string, SqlCorrectionResult>(testBtn, learnerSolution, onSqlCorrectionSuccess);
}

domReady(() => {
    initShowSampleSolBtn<StringSampleSolution[]>(sqlSamples =>
        sqlSamples.map(displayStringSampleSolution).join('\n')
    );

    editor = initEditor('text/x-mysql', 'textEditor');
    editor.on('change', () => {
        solutionChanged = true;
    })

    document.querySelector<HTMLAnchorElement>('#endSolveAnchor').onclick = () => {
        return !solutionChanged || confirm('Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?');
    };

    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;
});
