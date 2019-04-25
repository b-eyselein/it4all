import * as $ from 'jquery';
import * as CodeMirror from 'codemirror';
import {CompleteRunResult, instantiateAll} from "./simulator";
import {initEditor} from "../editorHelpers";

import 'codemirror/mode/python/python';
import {domReady} from "../otherHelpers";

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

    const solution: string = editor.getValue();
    // = {
    //     languague: "PYTHON_3",
    //     implementation: editor.getValue()
    // };


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
        success: onRoseCorrectionSuccess,
        error: onRoseCorrectionError
    });

}

domReady(() => {
    editor = initEditor('python', 'textEditor');

    testBtn = $('#testBtn');
    testBtn.on('click', testSol);
});
