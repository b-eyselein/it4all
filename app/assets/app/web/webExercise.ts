import * as $ from 'jquery';

import * as CodeMirror from 'codemirror';
import 'codemirror/mode/htmlmixed/htmlmixed';

import {renderWebCompleteResult, WebCompleteResult} from "./webCorrection";

import {focusOnCorrection, testTextExerciseSolution} from '../textExercise';

import {editor, uploadFiles} from '../tools/ideExercise';

let uploadBtn: HTMLButtonElement;

let previewChangedDiv: JQuery;
let showSampleSolBtn: JQuery;

let previewIsUpToDate: boolean = false;
let solutionChanged: boolean = false;

$(() => {
    previewChangedDiv = $('#previewChangedDiv');

    // FIXME: activate?
    // editor.on('change', () => {
    //     solutionChanged = true;
    //     if (previewIsUpToDate) {
    //         previewIsUpToDate = false;
    //         previewChangedDiv.prop('hidden', false);
    //     }
    // });


    document.getElementById('endSolveBtn').onclick = () => {
        return !solutionChanged || confirm("Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?");
    };

    document.getElementById('previewTabBtn').onclick = updatePreview;

    uploadBtn = document.getElementById('uploadBtn') as HTMLButtonElement;
    uploadBtn.onclick = testSol;
});


function testSol(): void {
    uploadBtn.disabled = true;

    const solution: string = editor.getValue();

    uploadFiles<WebCompleteResult>(uploadBtn, /*solution, */onWebCorrectionSuccess, onWebCorrectionError);
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
    $.ajax({
        type: 'PUT',
        contentType: 'text/plain', // type of message to server, "pure" html
        url: document.getElementById('previewTabBtn').dataset['url'],
        data: unescapeHTML(editor.getValue()),
        async: true,
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token);
        },
        success: () => {
            $('#preview').attr('src', function (i, val) {
                // Refresh iFrame
                return val;
            });
            previewIsUpToDate = true;
            previewChangedDiv.prop('hidden', true);
        },
        error: (jqXHR) => {
            console.error(jqXHR);
        }
    });
}
