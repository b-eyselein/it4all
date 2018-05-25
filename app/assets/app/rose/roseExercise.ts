import * as $ from 'jquery';
import * as CodeMirror from 'codemirror';
import {CompleteRunResult, instantiateAll} from "./simulator";
import {initEditor} from "../editorHelpers";

import 'codemirror/mode/python/python';

let testBtn: JQuery;
let editor: CodeMirror.Editor;


interface RoseCompleteResult {
    resultType: string
    cause: string
    result: CompleteRunResult
}

function onRoseCorrectionSuccess(completeResult: RoseCompleteResult): void {
    testBtn.prop('disabled', false);

    let correctionDiv = $('#correction');

    switch (completeResult.resultType) {
        case 'syntaxError':
            correctionDiv.html(`<div class="alert alert-danger"><b>Ihre Lösung hat einen Syntaxfehler:</b><hr><pre>${completeResult.cause}</pre></div>`);
            break;
        case 'success':
            let runResult = completeResult.result;
            if (runResult.correct) {
                correctionDiv.html(`<div class="alert alert-success">Ihre Lösung war korrekt.</div>`)
            } else {
                correctionDiv.html(`<div class="alert alert-danger">Ihre Lösung war nicht korrekt!</div>`)
            }
            instantiateAll(runResult);
            break;
        default:
            console.error('Unknown runresult type: ' + completeResult.resultType);
            break;
    }
}

function onRoseCorrectionError(jqXHR): void {
    console.error(jqXHR.responseText);
    testBtn.prop('disabled', false);
}

function testSol(): void {
    testBtn.prop('disabled', true);

    let dataToSend = {
        part: "",
        solution: {
            languague: "PYTHON",
            implementation: editor.getValue()
        }
    };

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: testBtn.data('url'),
        data: JSON.stringify(dataToSend),
        async: true,
        success: onRoseCorrectionSuccess,
        error: onRoseCorrectionError
    });

}

$(() => {
    editor = initEditor('python', 'roseEditor');

    testBtn = $('#testBtn');
    testBtn.on('click', testSol);
});