import * as $ from 'jquery';

import * as CodeMirror from 'codemirror';
import {initEditor} from "../editorHelpers";
import 'codemirror/mode/htmlmixed/htmlmixed';

import {renderWebCompleteResult, WebCompleteResult} from "./webCorrection";

import {focusOnCorrection, testTextExerciseSolution} from '../textExercise';

let editor: CodeMirror.Editor;

let testBtn: JQuery;
let previewChangedDiv: JQuery;
let showSampleSolBtn: JQuery;

let previewIsUpToDate: boolean = false;
let solutionChanged: boolean = false;

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
        return !solutionChanged || confirm("Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?");
    });

    $('#previewTabBtn').on('click', updatePreview);

    testBtn.on('click', testSol);
});


function testSol(): void {
    let testButton = $('#testBtn');
    testButton.prop('disabled', true);

    const solution: string = editor.getValue();

    testTextExerciseSolution(testButton, solution, onWebCorrectionSuccess, onWebCorrectionError);
}

function onWebCorrectionSuccess(result: WebCompleteResult): void {
    solutionChanged = false;

    testBtn.prop('disabled', false);

    renderWebCompleteResult(result);
    focusOnCorrection();
}

function onWebCorrectionError(jqXHR): void {
    $('#testBtn').prop('disabled', false);

    $('#correction').html(`
<div class="alert alert-danger">Es gab einen Fehler bei der Korrekur:
    <hr>
    <pre>${jqXHR.responseJSON.msg}</pre>
</div>`.trim())

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
        url: $('#previewTabBtn').data('url'),
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