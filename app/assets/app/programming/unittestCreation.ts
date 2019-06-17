import * as CodeMirror from 'codemirror';
import 'codemirror/mode/python/python';

import {domReady, focusOnCorrection, initShowSampleSolBtn, testExerciseSolution} from '../otherHelpers';

import {getIdeWorkspace, IdeWorkspace, setupEditor} from "../tools/ideExercise";

import {ProgCorrectionResult} from "./progImplementationCorrectionHandler";
import {ProgSampleSolution, showProgSampleSolutions} from './progSampleSolHelper';
import {renderUnitTestCorrectionResult} from './progUnitTestCorrectionHandler';


let editor: CodeMirror.Editor;
let testBtn: HTMLButtonElement;

let solutionChanged: boolean = false;

function onUnitTestCorrectionSuccess(result: ProgCorrectionResult): void {
    solutionChanged = false;

    document.querySelector<HTMLDivElement>('#correction').innerHTML = renderUnitTestCorrectionResult(result);

    focusOnCorrection();
}


domReady(() => {
    setupEditor().then((theEditor: void | CodeMirror.Editor) => {
        if (theEditor) {
            editor = theEditor;
            editor.on('change', () => {
                solutionChanged = true;
            });
        }
    });

    document.querySelector<HTMLAnchorElement>('#endSolveAnchor').onclick = () => {
        return !solutionChanged || confirm('Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?');
    };

    initShowSampleSolBtn<ProgSampleSolution[]>(showProgSampleSolutions);

    testBtn = document.querySelector<HTMLButtonElement>('#uploadBtn');
    testBtn.onclick = () => {
        const solution: IdeWorkspace = getIdeWorkspace();

        testExerciseSolution<IdeWorkspace, ProgCorrectionResult>(testBtn, solution, onUnitTestCorrectionSuccess);
    };

});
