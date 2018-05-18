import * as $ from 'jquery';
import * as CodeMirror from 'codemirror';
import {initEditor} from "../editorHelpers";
import 'codemirror/mode/htmlmixed/htmlmixed';

let editor: CodeMirror.Editor;

$(() => {
    editor = initEditor('htmlmixed', 'htmlEditor');
    $('#previewTabBtn').on('click', updatePreviewNew);
    $('#testBtn').on('click', testSol);
});

interface WebCompleteResult {
    solutionSaved: boolean
    part: string
    success: string
    points: number
    maxPoints: number
    htmlResults: HtmlResult[]
    jsResults: JsResult[]
}

interface WebResult {
    id: number
    points: number
    maxPoints: number
    success: boolean

}

interface HtmlResult extends WebResult {
    elementFound: boolean
    textContent: TextResult | null
    attributeResults: AttributeResult[]
}

interface TextResult {
    success: boolean
    awaited: string
    found: string | null
}

interface AttributeResult {
    success: boolean
    attrName: string
    awaited: string
    found: string | null
}


interface JsResult extends WebResult {
    preResults: ConditionResult[]
    actionDescription: string
    actionPerformed: boolean
    postResults: ConditionResult[]
}


interface ConditionResult {
    points: number
    maxPoints: number
    success: boolean
    description: string
    awaited: string
    gotten: string
}

function renderTextResult(textContent: TextResult): string {
    if (textContent.success) {
        return `<p><span class="glyphicon glyphicon-ok"></span> Das Element hat den richtigen Textinhalt.</p>`
    } else {
        return `
<p><span class="glyphicon glyphicon-remove"></span> Das Element hat nicht den richtigen Textinhalt.
    <ul>
        <li>Gesucht war:    &quot;${textContent.awaited}&quot;</li>
        <li>Gefunden wurde ${textContent.found != null ? (': &quot;' + textContent.found + '&quot;') : '<b>Kein Textinhalt!</b>'}&quot;</li>
    </ul>
</p>`.trim();
    }
}


function renderAttrResult(attrResult: AttributeResult): string {
    if (attrResult.success) {
        return `<p><span class="glyphicon glyphicon-ok"></span> Das Attribut &quot;${attrResult.attrName}&quot; hat den richtigen Wert.</p>`
    } else {
        return `
<p><span class="glyphicon glyphicon-remove"></span> Das Attribut &quot;${attrResult.attrName}&quot; hat nicht den richtigen Wert.
    <ul>
        <li>Gesucht war:    &quot;${attrResult.awaited}&quot;</li>
        <li>Gefunden wurde ${attrResult.found != null ? (': &quot;' + attrResult.found + '&quot;') : ' <b>kein Attribut!</b>'}</li>
    </ul>
</p>`.trim();
    }
}


function renderHtmlResult(result: HtmlResult): string {
    let subHtml: string = `<div class="alert alert-${result.success ? 'success' : 'danger'}">`;

    subHtml += `<b>Teilaufgabe ${result.id}</b>: ${$('#text_' + result.id).text()} (${ result.points} / ${result.maxPoints} Punkte)<hr>`;

    if (result.elementFound) {
        subHtml += `<p><span class="glyphicon glyphicon-ok"></span> Das Element konnte gefunden werden.</p>`;
    } else {
        subHtml += `<p><span class="glyphicon glyphicon-remove"></span> Das Element konnte nicht gefunden werden!</p>`;
    }

    if (result.textContent !== null) {
        subHtml += renderTextResult(result.textContent);
    }

    for (let attrResult of result.attributeResults) {
        subHtml += renderAttrResult(attrResult);
    }

    subHtml += `</div>`;
    return subHtml;
}


function renderConditionResult(condRes: ConditionResult): string {
    if (condRes.success) {
        return `<p><span class="glyphicon glyphicon-ok"></span> ${condRes.description}&quot;</p>`
    } else {
        return `
<p><span class="glyphicon glyphicon-remove"></span> ${condRes.description}&quot;
    <ul>
        <li>Gesucht war:    &quot;${condRes.awaited}&quot;</li>
        <li>Gefunden wurde aber: &quot;${condRes.gotten}&quot;</li>
    </ul>
</p>`.trim();
    }
}

function renderJsResult(result: JsResult): string {
    let subHtml: string = `<div class="alert alert-${result.success ? 'success' : 'danger'}">`;

    subHtml += `<b>Test ${result.id}:</b> (${ result.points} / ${result.maxPoints} Punkte)<hr>`;

    for (let preResult of result.preResults) {
        subHtml += renderConditionResult(preResult);
    }

    if (result.actionDescription) {
        subHtml += `<p><span class="glyphicon glyphicon-ok"></span> ${result.actionDescription}&quot;</p>`;
    } else {
        subHtml += `<p><span class="glyphicon glyphicon-remove"></span> ${result.actionDescription}&quot;</p>`;
    }

    for (let postResult of result.postResults) {
        subHtml += renderConditionResult(postResult);
    }

    subHtml += ` </div>`;
    return subHtml;
}

function renderResults(results: WebResult[], renderFunc: (WebResult) => string) {
    let html = '';

    for (let i = 0; i < results.length; i = i + 3) {
        let secondToRender = results[i + 1] || null;
        let thirdToRender = results[i + 2] || null;
        html += `
<div class="row">
    <div class="col-md-4">${renderFunc(results[i])}</div>
    <div class="col-md-4">${secondToRender === null ? '' : renderFunc(secondToRender)}</div>
    <div class="col-md-4">${thirdToRender === null ? '' : renderFunc(thirdToRender)}</div>
</div>`.trim();
    }

    return html;
}


function onWebCorrectionSuccess(corr: WebCompleteResult): void {
    let html: string = '';

    if (corr.solutionSaved) {
        html += `<div class="alert alert-success">Ihre Lösung wurde gespeichert.</div>`;
    } else {
        html += `<div class="alert alert-danger">Ihre Lösung konnte nicht gespeichert werden!</div>`;
    }

    if (corr.success) {
        html += `<div class="alert alert-success">Ihre Lösung war komplett richtig. Sie haben ${corr.points} von ${corr.maxPoints} erreicht.</div>`;
    } else {
        if (corr.points < .75 * corr.maxPoints) {
            html += `<div class="alert alert-danger">Ihre Lösung war nicht komplett richtig. Sie haben ${corr.points} von ${corr.maxPoints} erreicht.</div>`;
        } else {

            html += `<div class="alert alert-warning">Ihre Lösung war nicht komplett richtig. Sie haben ${corr.points} von ${corr.maxPoints} erreicht.</div>`;
        }

        if (corr.part === 'html') {
            html += renderResults(corr.htmlResults, renderHtmlResult);
        } else {
            html += renderResults(corr.jsResults, renderJsResult);
        }

    }

    $('#correction').html(html);
    $('#correctionTabBtn').trigger('click');
    $('#testBtn').prop('disabled', false);
}

function onWebCorrectionError(jqXHR): void {
    $('#testBtn').prop('disabled', false);

    $('#correction').html(`
<div class="alert alert-danger">Es gab einen Fehler bei der Korrekur:
    <hr>
    <pre>${jqXHR.responseJSON.msg}</pre>
</div>`.trim())
}

function testSol() {
    let testButton = $('#testBtn');
    testButton.prop('disabled', true);

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: testButton.data('url'),
        data: JSON.stringify({solution: editor.getValue()}),
        async: true,
        success: onWebCorrectionSuccess,
        error: onWebCorrectionError
    });
}

function unescapeHTML(escapedHTML: string): string {
    return escapedHTML.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
}

function updatePreviewNew(): void {
    $.ajax({
        type: 'PUT',
        contentType: 'text/plain', // type of message to server, "pure" html
        url: $('#previewTabBtn').data('url'),
        data: unescapeHTML(editor.getValue()),
        async: true,
        success: function () {
            $('#preview').attr('src', function (i, val) {
                // Refresh iFrame
                return val;
            });
        },
        error: function (jqXHR) {
            console.error(jqXHR);
        }
    });
}