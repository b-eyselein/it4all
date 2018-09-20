import * as $ from 'jquery';
import * as CodeMirror from 'codemirror';
import {initEditor} from "../editorHelpers";
import 'codemirror/mode/htmlmixed/htmlmixed';
import {onWebCorrectionError, renderWebCompleteResult, WebCompleteResult} from "./webCorrection";

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

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: testButton.data('url'),
        data: JSON.stringify(solution),
        async: true,
        success: onWebCorrectionSuccess,
        error: onWebCorrectionError
    });
}

function onWebCorrectionSuccess(result: WebCompleteResult): void {
    solutionChanged = false;

    testBtn.prop('disabled', false);

    renderWebCompleteResult(result);
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