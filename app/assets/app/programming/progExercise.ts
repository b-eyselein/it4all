import * as $ from 'jquery';
import {initEditor} from '../editorHelpers';
import * as CodeMirror from 'codemirror';
import 'codemirror/mode/python/python';

import {onProgCorrectionError, onProgCorrectionSuccess} from "./progCorrectionHandler";

let editor: CodeMirror.Editor;
let testBtn: JQuery, sampleSolBtn: JQuery;

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
    $('#correction').html('');
    testBtn.prop('disabled', true);

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: testBtn.data('url'),
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

function onShowSampleSolSuccess(response: string): void {
    sampleSolBtn.prop('disabled', false);

    $('#sampleSolDiv').prop('hidden', false);
    $('#sampleSolPre').html(response);
}

function onShowSampleSolError(jqXHR): void {
    sampleSolBtn.prop('disabled', false);
    console.error(jqXHR);
}

function showSampleSol(): void {
    sampleSolBtn.prop('disabled', true);
    $.ajax({
        type: 'GET',
        url: sampleSolBtn.data('url'),
        async: true,
        success: onShowSampleSolSuccess,
        error: onShowSampleSolError
    });
}

$(() => {
    editor = initEditor('python', 'myTextArea');
    testBtn = $('#testBtn');
    testBtn.on('click', testSol);

    sampleSolBtn = $('#sampleSolBtn');
    sampleSolBtn.on('click', showSampleSol);
});