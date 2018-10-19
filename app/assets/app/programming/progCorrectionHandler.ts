import {CorrectionResult} from "../matches";

export {renderProgCorrectionSuccess, ProgCorrectionResult, ProgStringSolution};


interface ProgStringSolution {
    language: string,
    implementation: string
}

function printValue(value: any): string {
    if (value == null) {
        return 'null';
    } else if (typeof value === 'string') {
        return `"${value}"`;
    } else if (Array.isArray(value)) {
        return '[' + value.map((v) => printValue(v)) + ']';
    } else {
        return value;
    }
}


interface ProgSingleResult {
    id: number
    successType: string
    correct: boolean
    input: object
    awaited: string
    gotten: string
    consoleOutput: string | null
}

interface ProgCorrectionResult extends CorrectionResult<ProgSingleResult> {
}

function renderProgResult(result: ProgSingleResult): string {
    let consoleOut = '';
    if (result.consoleOutput !== null) {
        consoleOut = '<p>Konsolenausgabe: <pre>' + result.consoleOutput + '</pre></p>';
    }

    let gottenResult;
    switch (result.successType) {
        case  "ERROR":
            gottenResult = `<p>Fehlerausgabe: <pre>${result.gotten}</pre></p>`;
            break;
        default:
            gottenResult = `<p>Bekommen: <code>${printValue(result.gotten)}</code></p>`;
    }

    return `
<div class="card">
    <div class="card-header bg-${result.correct ? 'success' : 'danger'}">${result.id}. Test war ${result.correct ? '' : ' nicht'} erfolgreich.</div>
    <div class="card-body">
        <p>Eingabe: <code>${printValue(result.input)}</code></p>
        <p>Erwartet: <code>${printValue(result.awaited)}</code></p>
        ${gottenResult}
        ${consoleOut}
    </div>
</div>`.trim();
}


function renderProgCorrectionSuccess(response: ProgCorrectionResult): string {

    let html: string = `<div class="alert alert-${response.solutionSaved ? 'success' : 'danger'}">Ihre Lösung wurde ${response.solutionSaved ? '' : ' nicht'} gespeichert.</div>`;

    const itemsPerRow = 2;
    const colWidth = 12 / itemsPerRow;

    for (let outerCount = 0; outerCount < response.results.length; outerCount = outerCount + itemsPerRow) {
        let innerHtml: string = '';

        for (let innerCount = outerCount; innerCount < outerCount + itemsPerRow && innerCount < response.results.length; innerCount++) {
            const currentResult = response.results[innerCount] || null;
            innerHtml += `<div class="col-md-${colWidth}">${currentResult != null ? renderProgResult(currentResult) : ''}</div>`
        }

        html += `<div class="row">${innerHtml}</div>`
    }

    return html;
}