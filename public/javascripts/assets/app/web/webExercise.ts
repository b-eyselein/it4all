import * as CodeMirror from 'codemirror';
import 'codemirror/mode/htmlmixed/htmlmixed';

import {WebCompleteResult, WebSampleSolution} from './webInterfaces';
import {renderWebCompleteResult} from './webCorrection';
import {domReady, escapeHtml, focusOnCorrection, initShowSampleSolBtn, testExerciseSolution} from '../otherHelpers';

import {ExerciseFile, getIdeWorkspace, IdeWorkspace, setupEditor} from '../tools/ideExercise';

let uploadBtn: HTMLButtonElement;
let previewChangedDiv: HTMLDivElement;

let previewIsUpToDate: boolean = false;
let solutionChanged: boolean = false;

let editor: CodeMirror.Editor;

function onWebCorrectionSuccess(result: WebCompleteResult): void {
    solutionChanged = false;

    uploadBtn.disabled = false;

    renderWebCompleteResult(result);
    focusOnCorrection();
}

function updatePreview(): void {
    const url = document.getElementById('previewTabBtn').dataset['url'];

    const token: string = document.querySelector<HTMLInputElement>('input[name="csrfToken"]').value;
    const headers: Headers = new Headers({
        'Content-Type': 'application/json',
        'Csrf-Token': token
    });

    const data: IdeWorkspace = getIdeWorkspace();

    fetch(url, {method: 'PUT', headers, body: JSON.stringify(data), credentials: 'same-origin'})
        .then(() => {
            document.querySelector<HTMLIFrameElement>('#preview').contentWindow.location.reload();
            previewIsUpToDate = true;
            previewChangedDiv.hidden = true;
        })
        .catch(reason => console.error(reason));
}

function showSampleSolFile(exFile: ExerciseFile): string {
    return `
<div class="card my-3">
    <div class="card-header">${exFile.name}</div>
    <div class="card-body bg-light">
        <pre>${escapeHtml(exFile.content)}</pre>
    </div>
</div>`;
}

function showSampleSolution(webSampleSolutions: WebSampleSolution[]): string {
    return webSampleSolutions
        .map<string>(webSampleSol =>
            webSampleSol.sample
                .map<string>(showSampleSolFile)
                .join('\n')
        )
        .join('\n');
}

domReady(() => {
    previewChangedDiv = document.querySelector<HTMLDivElement>('#previewChangedDiv');

    setupEditor().then((theEditor: void | CodeMirror.Editor) => {

            if (theEditor) {
                editor = theEditor;

                editor.on('change', () => {
                    solutionChanged = true;
                    if (previewIsUpToDate) {
                        previewIsUpToDate = false;
                        previewChangedDiv.hidden = false;
                    }
                });
            }
        }
    );

    document.getElementById('endSolveAnchor').onclick = () => {
        return !solutionChanged || confirm('Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?');
    };

    document.getElementById('previewTabBtn').onclick = updatePreview;

    uploadBtn = document.getElementById('uploadBtn') as HTMLButtonElement;
    uploadBtn.onclick = () => {
        testExerciseSolution<IdeWorkspace, WebCompleteResult>(uploadBtn, getIdeWorkspace(), onWebCorrectionSuccess);
    };

    initShowSampleSolBtn<WebSampleSolution[]>(showSampleSolution);
});
