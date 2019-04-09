import * as CodeMirror from 'codemirror';
import 'codemirror/mode/htmlmixed/htmlmixed';

import {renderWebCompleteResult, WebCompleteResult} from "./webCorrection";
import {focusOnCorrection} from '../textExercise';

import {getIdeWorkspace, IdeWorkspace, setupEditor, uploadFiles} from '../tools/ideExercise';

let uploadBtn: HTMLButtonElement;

let previewChangedDiv: HTMLDivElement;

let previewIsUpToDate: boolean = false;
let solutionChanged: boolean = false;

let editor: CodeMirror.Editor;

function domReady(fn: () => void): void {
    if (document.readyState === 'loading') {
        document.addEventListener("DOMContentLoaded", fn);
    } else {
        fn();
    }
}

domReady(() => {
    previewChangedDiv = document.querySelector<HTMLDivElement>('#previewChangedDiv');

    setupEditor().then((theEditor: CodeMirror.Editor) => {

            editor = theEditor;

            editor.on('change', () => {
                solutionChanged = true;
                if (previewIsUpToDate) {
                    previewIsUpToDate = false;
                    previewChangedDiv.hidden = false;
                }
            });
        }
    );

    document.getElementById('endSolveBtn').onclick = () => {
        return !solutionChanged || confirm("Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?");
    };

    document.getElementById('previewTabBtn').onclick = updatePreview;

    uploadBtn = document.getElementById('uploadBtn') as HTMLButtonElement;
    uploadBtn.onclick = testSol;
});


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

function unescapeHTML(escapedHTML: string): string {
    return escapedHTML
        .replace(/&amp;/g, '&')
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .replace(/&quot;/g, "\"")
        .replace(/&#039;/g, "'");
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
            console.info("Preview updated?");
        })
        .catch(reason => {
            console.error(reason);
        });
}
