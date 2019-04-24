import * as CodeMirror from 'codemirror';
import 'codemirror/mode/htmlmixed/htmlmixed';

import {WebCompleteResult, WebSampleSolution} from './webInterfaces';
import {renderWebCompleteResult} from './webCorrection';
import {domReady, escapeHtml, initShowSampleSolBtn} from '../otherHelpers';

import {ExerciseFile, IdeWorkspace} from '../tools/ideExerciseHelpers';
import {focusOnCorrection, getIdeWorkspace, setupEditor, uploadFiles} from '../tools/ideExercise';

let uploadBtn: HTMLButtonElement;
let showSampleSolBtn: HTMLButtonElement;

let previewChangedDiv: HTMLDivElement;

let previewIsUpToDate: boolean = false;
let solutionChanged: boolean = false;

let editor: CodeMirror.Editor;

function testSol(): void {
    uploadBtn.disabled = true;

    uploadFiles<WebCompleteResult>(uploadBtn, onWebCorrectionSuccess, onWebCorrectionError);
}

function onWebCorrectionSuccess(result: WebCompleteResult): void {
    solutionChanged = false;

    uploadBtn.disabled = false;

    renderWebCompleteResult(result);
    focusOnCorrection();
}

function onWebCorrectionError(jqXHR): void {
    uploadBtn.disabled = false;

    document.getElementById('correction').innerHTML = `
<div class="alert alert-danger">Es gab einen Fehler bei der Korrekur:
    <hr>
    <pre>${jqXHR.responseJSON.msg}</pre>
</div>`.trim();

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

    fetch(url, {method: 'PUT', headers, body: JSON.stringify(data)})
        .then(() => {
            document.querySelector<HTMLIFrameElement>('#preview').contentWindow.location.reload();
            previewIsUpToDate = true;
            previewChangedDiv.hidden = true;
        })
        .catch(reason => {
            console.error(reason);
        });
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
    uploadBtn.onclick = testSol;

    initShowSampleSolBtn<WebSampleSolution[]>(showSampleSolution);
});
