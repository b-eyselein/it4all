import * as $ from 'jquery';

import {ConditionResult, HtmlResult, JsResult, TextResult, WebCompleteResult, WebResult} from './webInterfaces';


function dispPoints(result: WebResult): string {
    return '(' + result.points + " / " + result.maxPoints + ' Punkte)';
}

function renderTextResult(textContentResult: TextResult): string {
    if (textContentResult.isSuccessful) {
        return `<span class="text-success">Das Element hat den richtigen Textinhalt.</span>`
    } else {
        let foundRender: string;
        if (textContentResult.maybeFoundContent === null || textContentResult.maybeFoundContent.length === 0) {
            foundRender = '<b>kein Textinhalt!</b>'
        } else {
            foundRender = `&quot;<code>${textContentResult.maybeFoundContent}</code>&quot;`;
        }

        return `
<span class="text-danger">
    Das Element hat nicht den richtigen Textinhalt:
    <ul>
        <li>Gesucht war &quot;<code>${textContentResult.awaitedContent}</code>&quot;,</li>
        <li>gefunden wurde ${foundRender}</li>
    </ul>
</span>`.trim();
    }
}


function renderAttrResult(attrResult: TextResult): string {
    if (attrResult.isSuccessful) {
        return `<span class="text-success">Das Attribut <code>${attrResult.keyName}</code> hat den richtigen Wert.</span>`
    } else {
        return `
<span class="text-danger">
    Das Attribut <code>${attrResult.keyName}</code> hat nicht den richtigen Wert:
    <ul>
        <li>Gesucht war <code>${attrResult.awaitedContent}</code>,</li>
        <li>gefunden wurde ${attrResult.maybeFoundContent != null ? (`<code>${attrResult.maybeFoundContent}</code>`) : ' <b>kein Attribut!</b>'}</li>
    </ul>
</span>`.trim();
    }
}

function renderHtmlResult(result: HtmlResult): string {
    if (result.success) {
        return `<span class="text-success">Teilaufgabe ${result.id} ${dispPoints(result)} ist korrekt.</span>`;
    } else {

        let elFoundText: string, textResult: string,
            attrResults: string[] = result.attributeResults.map(renderAttrResult);

        if (result.elementFound) {
            elFoundText = `<span class="text-success">Das Element konnte gefunden werden.</span>`;
        } else {
            elFoundText = `<span class="text-danger">Das Element konnte nicht gefunden werden!</span>`;
        }


        if (result.textContentResult !== null) {
            textResult = `<li>${renderTextResult(result.textContentResult)}</li>`;
        } else {
            textResult = '';
        }


        return `
<span class="text-danger">Teilaufgabe ${result.id} ${dispPoints(result)}</span>
<ul>
    <li>${elFoundText}</li>
    ${textResult}
    ${attrResults.map(a => `<li>${a}</li>`).join("")}
</ul>`.trim();
    }
}


function renderConditionResult(condRes: ConditionResult): string {
    if (condRes.success) {
        return `<span class="text-success">${condRes.description}</span>`
    } else {
        return `
<span class="text-danger">
    ${condRes.description}:
    Gesucht war <code>${condRes.awaited}</code>,
    gefunden wurde aber <code>${condRes.gotten}</code>
</span>`.trim();
    }
}

function renderJsResult(result: JsResult): string {
    if (result.success) {
        return `<span class="text-success">Test ${result.id} ${dispPoints(result)} war erfolgreich.</span>`;
    } else {

        let actionDesc: string;
        if (result.actionDescription) {
            actionDesc = `<span class="text-success">${result.actionDescription}</span>`;
        } else {
            actionDesc = `<span class="text-danger">${result.actionDescription}</span>`;
        }

        return `
<span class="text-${result.success ? 'success' : 'danger'}">Test ${dispPoints(result)} war nicht erfolgreich:</span>
<ul>
    ${result.preResults.map(r => `<li>${renderConditionResult(r)}</li>`).join("")}
    <li>${actionDesc}</li>
    ${result.postResults.map(r => `<li>${renderConditionResult(r)}</li>`).join("")}
</ul>`.trim();
    }
}


export function renderWebCompleteResult(corr: WebCompleteResult): void {

    let html: string = '';

    let solutionSavedMsg: string, solSavedClass: string;
    if (corr.solutionSaved) {
        solSavedClass = 'success';
        solutionSavedMsg = 'Ihre Lösung wurde gespeichert.';
    } else {
        solSavedClass = 'danger';
        solutionSavedMsg = 'Ihre Lösung konnte nicht gespeichert werden!';
    }


    let pointMsgClass: string;
    if (corr.success) {
        pointMsgClass = 'success';
    } else if (corr.points < .75 * corr.maxPoints) {
        pointMsgClass = 'danger';
    } else {
        pointMsgClass = 'warning';
    }


    html += `
<p>
    <span class="text-${solSavedClass}">${solutionSavedMsg}</span>
    <span class="text-${pointMsgClass}">Sie haben ${corr.points} von ${corr.maxPoints} erreicht.</span>
</p>`.trim();

    if (!corr.success) {
        html += '<ul>';
        if (corr.part === 'html') {
            html += corr.htmlResults.map(r => `<li>${renderHtmlResult(r)}</li>`).join('\n');
        } else {
            html += corr.jsResults.map((r) => `<li>${renderJsResult(r)}</li>`).join('\n');
        }
        html += '</ul>';
    }

    $('#correction').html(html);
    $('#correctionDiv').prop('hidden', false);

}

