import * as CodeMirror from 'codemirror';
import {CompleteRunResult, instantiateAll} from "./simulator";
import {initEditor} from "../editorHelpers";

import 'codemirror/mode/python/python';
import {domReady, testExerciseSolution} from "../otherHelpers";

let testBtn: HTMLButtonElement;
let editor: CodeMirror.Editor;


interface RoseCompleteResult {
    result: CompleteRunResult
    points: number;
    maxPoints: number;
    solutionSaved: boolean;
}

function onRoseCorrectionSuccess(completeResult: RoseCompleteResult): void {
    console.warn(JSON.stringify(completeResult, null, 2));

    testBtn.disabled = false;

    const correctionDiv = document.querySelector<HTMLDivElement>('#correction');

    const runResult = completeResult.result;

    if (runResult.correct) {
        correctionDiv.innerHTML = (`<div class="alert alert-success">Ihre Lösung war korrekt.</div>`)
    } else {
        correctionDiv.innerHTML = (`<div class="alert alert-danger">Ihre Lösung war nicht korrekt!</div>`)
    }

    instantiateAll(runResult);
}

function testSol(): void {
    testBtn.disabled = true;

    const solution: string = editor.getValue();
    // = {
    //     languague: "PYTHON_3",
    //     implementation: editor.getValue()
    // };

    testExerciseSolution<string, RoseCompleteResult>(testBtn, solution, onRoseCorrectionSuccess);
}

domReady(() => {
    editor = initEditor('python', 'textEditor');

    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;
});
