import {initEditor} from '../editorHelpers';
import * as CodeMirror from 'codemirror';
import 'codemirror/mode/python/python';

import {ProgCorrectionResult, ProgSolution, renderProgCorrectionSuccess} from "./progCorrectionHandler";
import {domReady, focusOnCorrection, initShowSampleSolBtn, testExerciseSolution} from "../otherHelpers";
import {ExerciseFile} from "../tools/ideExerciseHelpers";

export {onProgCorrectionSuccess};

let editor: CodeMirror.Editor;
let testBtn: HTMLButtonElement;

let solutionChanged: boolean = false;

export interface ProgSampleSolution {
    id: number;
    base: string;
    solutionStr: string;
    unitTest: ExerciseFile
}

function testSol(): void {
    const solution: ProgSolution = {
        implementation: editor.getValue(),
        testData: [],
        unitTest: ''
    };

    testExerciseSolution<ProgSolution, ProgCorrectionResult>(testBtn, solution, onProgCorrectionSuccess)
}

function onProgCorrectionSuccess(result: ProgCorrectionResult): void {
    solutionChanged = false;

    document.querySelector<HTMLDivElement>('#correction').innerHTML = renderProgCorrectionSuccess(result);

    focusOnCorrection();
}

domReady(() => {
    editor = initEditor('python', 'textEditor');
    editor.on('change', () => {
        solutionChanged = true;
    });

    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;

    initShowSampleSolBtn<ProgSampleSolution[]>((samples: ProgSampleSolution[]) => {
        return samples.map<string>((s: ProgSampleSolution) => {
            return `
<div class="card">
    <div class="card-body bg-light">
        <pre>${s.solutionStr}</pre>
    </div>
</div>`.trim();
        }).join('\n');
    });

    document.querySelector<HTMLAnchorElement>('#endSolveAnchor').onclick = () => {
        return !solutionChanged || confirm("Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?");
    };
});
