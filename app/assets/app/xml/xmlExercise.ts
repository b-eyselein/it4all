import * as $ from 'jquery';

import {initEditor} from "../editorHelpers";

import * as CodeMirror from 'codemirror';
import 'codemirror/mode/xml/xml';

let editor: CodeMirror.Editor;

interface XmlError {
    errorType: 'WARNING' | 'ERROR' | 'FATAL',
    errorMessage: string,
    line: number,
    success: string
}

interface XmlDocumentCorrectionResponse {
    solSaved: boolean,
    success: boolean,
    results: XmlError[]
}

function onXmlDocumentCorrectionSuccess(response: XmlDocumentCorrectionResponse): void {
    let html: string = '';

    if (response.solSaved) {
        html += `<div class="alert alert-success">Ihre Lösung wurde gespeichert.</div>`;
    } else {
        html += `<div class="alert alert-danger">Ihre Lösung konnte nicht gespeichert werden!</div>`;
    }

    if (response.success) {
        html += `<div class="alert alert-success">Die Korrektur war erfolgreich. Es wurden keine Fehler gefunden.</div>`;
    } else {
        html += response.results.map((xmlError) => {
            let cls = xmlError.errorType === 'WARNING' ? 'warning' : 'danger';
            return `<div class="alert alert-${cls}"><b>Fehler in Zeile ${xmlError.line}</b>: ${xmlError.errorMessage}</div>`;
        }).join('\n');
    }

    $('#correction').html(html);
}


function onXmlGrammarCorrectionSuccess(response): void {
    console.log(response);
}

function onXmlCorrectionError(jqXHR): void {
    $('#correctionDiv').html(`<div class="alert alert-danger">${jqXHR.responseJSON.msg}</div>`);
}


function testSol(): void {
    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: $('#testBtn').data('url'),
        data: JSON.stringify({solution: editor.getValue()}),
        async: true,
        success: ($('#exercisePart').val() === 'document') ? onXmlDocumentCorrectionSuccess : onXmlGrammarCorrectionSuccess,
        error: onXmlCorrectionError
    });
}

$(() => {
    editor = initEditor('xml');
    $('#testBtn').on('click', testSol);
});