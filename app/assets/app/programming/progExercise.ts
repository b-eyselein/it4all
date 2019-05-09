import {initEditor} from '../editorHelpers';
import * as CodeMirror from 'codemirror';
import 'codemirror/mode/python/python';

import {ProgCorrectionResult, ProgSolution, renderProgCorrectionSuccess} from "./progCorrectionHandler";
import {
    displayStringSampleSolution,
    domReady, focusOnCorrection,
    initShowSampleSolBtn,
    SampleSolution,
    testExerciseSolution
} from "../otherHelpers";

import {ExerciseFile, IdeWorkspace} from '../tools/ideExerciseHelpers';
import {getIdeWorkspace, setupEditor} from '../tools/ideExercise';

export {onProgCorrectionSuccess};

let editor: CodeMirror.Editor;
let testBtn: HTMLButtonElement;

let solutionChanged: boolean = false;

interface ProgSampleSolution {
    id: number;
    base: string;
    solutionStr: string;
}


function onProgCorrectionSuccess(result: ProgCorrectionResult): void {
    solutionChanged = false;

    document.querySelector<HTMLDivElement>('#correction').innerHTML = renderProgCorrectionSuccess(result);

    focusOnCorrection();
}

function showProgSampleSolution(s: ProgSampleSolution): string {
    return `
<div class="card">
    <div class="card-body bg-light">
        <pre>${s.solutionStr}</pre>
    </div>
</div>`.trim();
}

domReady(() => {
    setupEditor().then((theEditor: null | CodeMirror.Editor) => {
        if (theEditor) {
            editor = theEditor;

            editor.on('change', () => {
                solutionChanged = true;
            });
        }
    });

    testBtn = document.querySelector<HTMLButtonElement>('#uploadBtn');
    testBtn.onclick = () => {
        testExerciseSolution<IdeWorkspace, ProgCorrectionResult>(testBtn, getIdeWorkspace(), onProgCorrectionSuccess);
    };

    initShowSampleSolBtn<ProgSampleSolution[]>((samples: ProgSampleSolution[]) => {
        return samples.map<string>(showProgSampleSolution).join('\n');
    });

    document.querySelector<HTMLAnchorElement>('#endSolveAnchor').onclick = () => {
        return !solutionChanged || confirm("Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?");
    };
});
