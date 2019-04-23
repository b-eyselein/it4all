import * as CodeMirror from 'codemirror';
import {initEditor} from '../editorHelpers';

import {focusOnCorrection, initShowSampleSolBtn, testTextExerciseSolution} from "../textExercise";
import {domReady} from "../otherHelpers";

let editor: CodeMirror.Editor;

let testBtn: HTMLButtonElement;

let solutionChanged: boolean = false;

interface RegexSampleSolution {
    id: number;
    sample: string;
}

interface RegexSingleCorrectionResult {
    testData: string
    included: boolean
    resultType: 'TruePositive' | 'FalsePositive' | 'FalseNegative' | 'TrueNegative'
}

interface RegexCorrectionResult {
    solutionSaved: boolean
    solution: string
    points: number
    maxPoints: number
    results: RegexSingleCorrectionResult[]
}

function testSol(): void {
    const solution: string = editor.getValue().trim();

    if (solution.length === 0) {
        alert('Sie können keine leere Lösung abgeben!');
        return;
    }

    testBtn.disabled = true;

    testTextExerciseSolution<string, RegexCorrectionResult>(testBtn, solution, onRegexCorrectionSuccess);
}

function onRegexCorrectionSuccess(correctionResult: RegexCorrectionResult): void {
    testBtn.disabled = false;

    solutionChanged = false;

    let html: string = '';

    // Was solution saved?
    const solSaved = correctionResult.solutionSaved;
    html += `<p class="${solSaved ? 'text-success' : 'text-danger'}">Ihre Lösung wurde ${solSaved ? '' : 'nicht'}gespeichert.</p>`;

    // How many (max) points?
    html += `<p>Sie haben ${correctionResult.points} von ${correctionResult.maxPoints} Punkten erreicht.</p>`;

    // Single results
    for (const result of correctionResult.results) {

        let toAdd: string;
        let clazz: string;

        switch (result.resultType) {
            case 'TruePositive':
                toAdd = 'wurde korrekt erkannt.';
                clazz = 'text-success';
                break;
            case 'FalsePositive':
                toAdd = 'wurde fälschlicherweise erkannt.';
                clazz = 'text-danger';
                break;
            case 'FalseNegative':
                toAdd = 'wurde fälschlicherweise <b>nicht</b> erkannt.';
                clazz = 'text-danger';
                break;
            case 'TrueNegative':
                toAdd = 'wurde korrekt <b>nicht</b> erkannt.';
                clazz = 'text-success';
                break;
        }
        html += `<p class="${clazz}"><code>${result.testData}</code> ${toAdd}</p>`;
    }

    document.querySelector<HTMLDivElement>('#correctionDiv').innerHTML = html;
    focusOnCorrection();
}

function displayRegexSampleSolution(s: RegexSampleSolution): string {
    return `
<div class="card my-3">
    <div class="card-body bg-light">
        <pre>${s.sample.trim()}</pre>
    </div>
</div>`.trim();
}

domReady(() => {
    initShowSampleSolBtn<RegexSampleSolution[]>((regexSampleSolutions: RegexSampleSolution[]) =>
        regexSampleSolutions.map(displayRegexSampleSolution).join('\n')
    );

    editor = initEditor('', 'textEditor');

    document.querySelector<HTMLAnchorElement>('#endSolveAnchor').onclick = () => {
        return !solutionChanged || confirm('Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?');
    };

    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;
});
