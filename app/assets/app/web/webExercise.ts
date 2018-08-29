import * as $ from 'jquery';
import * as CodeMirror from 'codemirror';
import {initEditor} from "../editorHelpers";
import 'codemirror/mode/htmlmixed/htmlmixed';
import {onWebCorrectionError, onWebCorrectionSuccess} from "./webCorrection";

let editor: CodeMirror.Editor;
let previewIsUpToDate = false;

let previewChangedDiv;

$(() => {
    previewChangedDiv = $('#previewChangedDiv');

    editor = initEditor('htmlmixed', 'htmlEditor');
    editor.on('change', () => {
        if (previewIsUpToDate) {
            previewIsUpToDate = false;
            previewChangedDiv.prop('hidden', false);
        }
    });

    $('#previewTabBtn').on('click', updatePreview);
    $('#testBtn').on('click', testSol);
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

function unescapeHTML(escapedHTML: string): string {
    return escapedHTML.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
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