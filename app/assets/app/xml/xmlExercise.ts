import * as $ from 'jquery';

import * as CodeMirror from 'codemirror';
import 'codemirror/mode/xml/xml';
import 'codemirror/mode/dtd/dtd';
import {initEditor} from "../editorHelpers";

import {CorrectionResult} from "../matches";

import {renderXmlGrammarCorrectionSuccess, XmlGrammarCorrectionResult} from "./xmlGrammarCorrection";

let editor: CodeMirror.Editor;

let testBtn: JQuery;
let endSolveBtn: JQuery;

let solutionChanged: boolean;

interface XmlError {
    errorType: 'WARNING' | 'ERROR' | 'FATAL',
    errorMessage: string,
    line: number,
    success: string
}

interface XmlDocumentCorrectionResponse extends CorrectionResult<XmlError> {
    results: XmlError[]
}

function onXmlDocumentCorrectionSuccess(response: XmlDocumentCorrectionResponse): void {
    solutionChanged = false;
    testBtn.prop('disabled', false);

    let html: string = '';

    if (response.solutionSaved) {
        html += `<div class="alert alert-success">Ihre Lösung wurde gespeichert.</div>`;
    } else {
        html += `<div class="alert alert-danger">Ihre Lösung konnte nicht gespeichert werden!</div>`;
    }

    if (response.success) {
        html += `<div class="alert alert-success">Die Korrektur war erfolgreich. Es wurden keine Fehler gefunden.</div>`;
    } else {
        html += response.results.map((xmlError: XmlError) => {
            const cls = xmlError.errorType === 'WARNING' ? 'warning' : 'danger';
            return `<div class="alert alert-${cls}"><b>Fehler in Zeile ${xmlError.line}</b>: ${xmlError.errorMessage}</div>`;
        }).join('\n');
    }

    $('#correction').html(html);
}


function onXmlGrammarCorrectionSuccess(response: XmlGrammarCorrectionResult): void {
    solutionChanged = false;
    testBtn.prop('disabled', false);

    $('#correction').html(renderXmlGrammarCorrectionSuccess(response));
}

function onXmlCorrectionError(jqXHR): void {
    testBtn.prop('disabled', false);

    $('#correctionDiv').html(`<div class="alert alert-danger">${jqXHR.responseJSON.msg}</div>`);
}


function testSol(): void {
    testBtn.prop('disabled', true);

    const isDocumentPart: boolean = $('#exercisePart').val() === 'document';

    const solution: string = editor.getValue();

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: testBtn.data('url'),
        data: JSON.stringify(solution),
        async: true,
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token)
        },
        success: isDocumentPart ? onXmlDocumentCorrectionSuccess : onXmlGrammarCorrectionSuccess,
        error: onXmlCorrectionError
    });
}

function endSolve(): boolean {
    return !solutionChanged || confirm("Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?");
}

$(() => {
    const language: string = ($('#exercisePart').val() === 'grammar') ? 'application/xml-dtd' : 'xml';

    editor = initEditor(language, 'textEditor');
    editor.on('change', () => {
        solutionChanged = true;
    });

    testBtn = $('#testBtn');
    testBtn.on('click', testSol);

    endSolveBtn = $('#endSolveBtn');
    endSolveBtn.on('click', endSolve);
});