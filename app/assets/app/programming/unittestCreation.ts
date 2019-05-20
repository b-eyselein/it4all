import * as CodeMirror from 'codemirror';
import 'codemirror/mode/python/python';
import {domReady, focusOnCorrection, initShowSampleSolBtn, testExerciseSolution} from '../otherHelpers';
import {getIdeWorkspace, setupEditor} from "../tools/ideExercise";
import {IdeWorkspace} from '../tools/ideExerciseHelpers';
import {ProgCorrectionResult, UnitTestCorrectionResult} from "./progCorrectionHandler";
import {ProgSampleSolution} from './progExercise';

let editor: CodeMirror.Editor;

let testBtn: HTMLButtonElement;

let solutionChanged: boolean = false;


function onUnitTestCorrectionSuccess(result: ProgCorrectionResult): void {

    console.info(JSON.stringify(result, null, 2));

    let html = '';

    if (result.solutionSaved) {
        html += `<p class="text-success">Ihre Lösung wurde gespeichert.</p>`;
    } else {
        html += `<p class="text-danger">Ihre Lösung konnte nicht gespeichert werden.</p>`;
    }

    const numOfSuccessfulTests: number = result.unitTestResults.filter(r => r.successful).length;
    const progressPercentage: number = Math.round(numOfSuccessfulTests * 100 / result.unitTestResults.length);

    let progressBarColor: string = 'bg-danger';
    if (50 <= progressPercentage && progressPercentage < 100) {
        progressBarColor = 'bg-warning'
    } else if (progressPercentage == 100) {
        progressBarColor = 'bg-success';
    }

    html += `
<p class="text-${progressBarColor}">Sie haben ${numOfSuccessfulTests} von ${result.unitTestResults.length} Tests bestanden.</p>
<div class="progress">
    <div class="progress-bar ${progressBarColor}" style="width: ${progressPercentage}%;">${progressPercentage}%</div>
</div>
`.trim();

    html += '<hr>';

    // FIXME: points === undefinded, maxPoints = undefined!
    console.info(result.points, result.maxPoints);

    html += '<ul>' + result.unitTestResults
        .map((unitTestCorrectionResult: UnitTestCorrectionResult) => {
            const successClass = unitTestCorrectionResult.successful ? 'success' : 'danger';
            const successWord = unitTestCorrectionResult.successful ? '' : 'nicht '

            let description = '';
            if (!unitTestCorrectionResult.successful) {
                description += `<p>${unitTestCorrectionResult.testConfig.description}</p>`
            }

            return `
<li>
    <p class="text-${successClass}">Der ${unitTestCorrectionResult.testConfig.id}. Test war ${successWord} erfolgreich.</p>
    ${description}
</li>`.trim();
        })
        .join("\n") + '</ul>';

    document.querySelector<HTMLDivElement>('#correction').innerHTML = html;

    solutionChanged = false;

    focusOnCorrection();
}

function showSampleSolution(sampleSols: ProgSampleSolution[]): string {
    console.error(JSON.stringify(sampleSols, null, 2));

    return sampleSols.map(sampleSol => `
<div class="card">
    <div class="card-body bg-light">
        <pre>${sampleSol.unitTest.content}</pre>
    </div>
</div>`.trim()
    ).join('\n');
}

domReady(() => {

    setupEditor().then((theEditor: void | CodeMirror.Editor) => {
        if (theEditor) {
            editor = theEditor;
            editor.on('change', () => {
                solutionChanged = true;
            });

            document.querySelector<HTMLButtonElement>('button[data-filename="test.py"]').click();
        }
    });

    document.getElementById('endSolveAnchor').onclick = () => {
        return !solutionChanged || confirm('Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?');
    };

    initShowSampleSolBtn<ProgSampleSolution[]>(showSampleSolution);


    testBtn = document.querySelector<HTMLButtonElement>('#uploadBtn');
    testBtn.onclick = () => {
        const solution: IdeWorkspace = getIdeWorkspace();

        testExerciseSolution<IdeWorkspace, ProgCorrectionResult>(testBtn, solution, onUnitTestCorrectionSuccess);
    };

});
