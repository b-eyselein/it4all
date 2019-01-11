import * as $ from 'jquery';

import * as CodeMirror from 'codemirror';
import {initEditor} from '../editorHelpers';

import {focusOnCorrection, testTextExerciseSolution} from "../textExercise";

let editor: CodeMirror.Editor;

let testBtn: JQuery;
let previewChangedDiv: JQuery;

let testButton: JQuery;

let previewIsUpToDate: boolean = false;
let solutionChanged: boolean = false;

interface RegexSingleCorrectionResult {
    testData: string
    included: boolean
    resultType: 'TruePositive' | 'FalsePositive' | 'FalseNegative' | 'TrueNegative'
}

interface RegexCorrectionResult {
    solution: string,
    results: RegexSingleCorrectionResult[]
}

$(() => {
    previewChangedDiv = $('#previewChangedDiv');
    testBtn = $('#testBtn');


    editor = initEditor('htmlmixed', 'textEditor');
    editor.on('change', () => {
        solutionChanged = true;
        if (previewIsUpToDate) {
            previewIsUpToDate = false;
            previewChangedDiv.prop('hidden', false);
        }
    });


    $('#endSolveBtn').on('click', () => {
        return !solutionChanged || confirm('Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?');
    });


    testBtn.on('click', testSol);
});


function testSol(): void {
    const solution: string = editor.getValue().trim();

    if (solution.length === 0) {
        alert('Sie können keine leere Lösung abgeben!');
        return;
    }

    testBtn.prop('disabled', true);

    testTextExerciseSolution(testBtn, solution, onRegexCorrectionSuccess, onRegexCorrectionError);
}

function onRegexCorrectionSuccess(correctionResult: RegexCorrectionResult): void {
    testBtn.prop('disabled', false);

    let html: string = '';

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


    $('#correctionDiv').html(html);
    focusOnCorrection();
}

function onRegexCorrectionError(jqXHR): void {
    testBtn.prop('disabled', false);

    console.error(jqXHR.responseText);
    focusOnCorrection();
}