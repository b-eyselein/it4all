import * as $ from 'jquery';
import {initEditor} from '../editorHelpers';

import {Ace} from 'ace';

import {onProgCorrectionError, onProgCorrectionSuccess} from "./progCorrectionHandler";

let editor: Ace.Editor;

function onChangeLanguageSuccess(response) {
    $('#language').val(response.language);
    editor.setValue(response, 1000000);
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
    let exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = ''; // jsRoutes.controllers.ExerciseController.correctLive('programming', exerciseId, exercisePart).url;

    $('#correction').html('');


    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url,
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
    editor = initEditor('python', 15, 30);
    $('#testBtn').on('click', testSol);
});