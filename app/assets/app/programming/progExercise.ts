import * as $ from 'jquery';
import {initEditor} from '../editorHelpers';
import * as CodeMirror from 'codemirror';
import 'codemirror/mode/python/python';

import {renderProgCorrectionSuccess, ProgCorrectionResult} from "./progCorrectionHandler";

export {ProgStringSolution, onProgCorrectionSuccess};

let editor: CodeMirror.Editor;
let testBtn: JQuery, sampleSolBtn: JQuery;

let solutionChanged: boolean = false;

interface ProgStringSolution {
    language: string,
    implementation: string
}

function onChangeLanguageSuccess(response) {
    $('#language').val(response.language);
    // editor.setValue(response, 1000000);
}

function changeProgLanguage(): void {
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

    const solution: ProgStringSolution = {
        language: $('#langSelect').val() as string,
        implementation: editor.getValue()
    };

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: testBtn.data('url'),
        data: JSON.stringify(solution),
        async: true,
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token);
        },
        success: onProgCorrectionSuccess,
        error: onProgCorrectionError
    });
}

function onProgCorrectionSuccess(result: ProgCorrectionResult): void {
    testBtn.prop('disabled', false);
    const html = renderProgCorrectionSuccess(result);

    $('#correctionDiv').prop('hidden', false);
    $('#correction').html(html);

    solutionChanged = false;
}

function onProgCorrectionError(jqXHR): void {
    console.error(jqXHR.responseText);
    testBtn.prop('disabled', false);
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
    editor = initEditor('python', 'textEditor');
    editor.on('change', () => {
        solutionChanged = true;
    });

    testBtn = $('#testBtn');
    testBtn.on('click', testSol);

    sampleSolBtn = $('#sampleSolBtn');
    sampleSolBtn.on('click', showSampleSol);

    $('#endSolveBtn').on('click', () => {
        return !solutionChanged || confirm("Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?");
    });
});