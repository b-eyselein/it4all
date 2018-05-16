import * as $ from 'jquery';
import {initEditor} from '../editorHelpers';
import * as CodeMirror from 'codemirror';
import 'codemirror/mode/python/python';

import {onProgCorrectionError, onProgCorrectionSuccess} from "./progCorrectionHandler";

let editor: CodeMirror.Editor;

function onChangeLanguageSuccess(response) {
    $('#language').val(response.language);
    // editor.setValue(response, 1000000);
}

function changeProgLanguage() {
    // @controllers.routes.ExerciseController.progGetDeclaration("")

    let url = '';

    $.ajax({
        type: 'GET',
        url,
        data: 'language=' + $('#langSelect').val(),
        async: true,
        success: onChangeLanguageSuccess
    });
}

function testSol(): void {
    let testButton = $('#testBtn');
    $('#correction').html('');
    testButton.prop('disabled', true);

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: testButton.data('url'),
        data: JSON.stringify({
            solution: {
                language: $('#langSelect').val(),
                implementation: editor.getValue()
            }
        }),
        async: true,
        success: onProgCorrectionSuccess,
        error: onProgCorrectionError
    });
}

$(() => {
    editor = initEditor('python');
    $('#testBtn').on('click', testSol);
});