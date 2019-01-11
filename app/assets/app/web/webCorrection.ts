import * as $ from 'jquery';

export {onWebCorrectionError, renderWebCompleteResult, WebCompleteResult};

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

function dispPoints(result: WebResult): string {
    return '(' + result.points + " / " + result.maxPoints + ' Punkte)';
}

function renderTextResult(textContent: TextResult): string {
    if (textContent.success) {
        return `<span class="text-success">Das Element hat den richtigen Textinhalt.</span>`
    } else {
        return `
<span class="text-danger">
    Das Element hat nicht den richtigen Textinhalt.
    Gesucht war <code>${textContent.awaited}</code>,
    gefunden wurde ${textContent.found != null ? (`<code>${textContent.found}</code>`) : '<b>Kein Textinhalt!</b>'}</code>
</span>`.trim();
    }
}


function renderAttrResult(attrResult: AttributeResult): string {
    if (attrResult.success) {
        return `<span class="text-success">Das Attribut <code>${attrResult.attrName}</code> hat den richtigen Wert.</span>`
    } else {
        return `
<span class="text-danger">
    Das Attribut <code>${attrResult.attrName}</code> hat nicht den richtigen Wert.
    Gesucht war <code>${attrResult.awaited}</code>
    gefunden wurde ${attrResult.found != null ? (`<code>${attrResult.found}</code>`) : ' <b>kein Attribut!</b>'}
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


        if (result.textContent !== null) {
            textResult = `<li>${renderTextResult(result.textContent)}</li>`;
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


function renderWebCompleteResult(corr: WebCompleteResult): void {
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

    // $('#correctionTabBtn').tab('show');
}

function onWebCorrectionError(jqXHR): void {
    $('#testBtn').prop('disabled', false);

    $('#correction').html(`
<div class="alert alert-danger">Es gab einen Fehler bei der Korrekur:
    <hr>
    <pre>${jqXHR.responseJSON.msg}</pre>
</div>`.trim())
}