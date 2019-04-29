import {CorrectionResult} from "../matches";

export {renderProgCorrectionSuccess, ProgCorrectionResult, ProgSolution};

interface ProgSolution {
    implementation: string,
    testData: any[]
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

function renderProgResult(result: ProgSingleResult): string {
    let consoleOut = '';
    if (result.consoleOutput !== null) {
        consoleOut = '<p>Konsolenausgabe: <pre>' + result.consoleOutput + '</pre></p>';
    }

    let gottenResult: string;
    if (result.successType === "ERROR") {
        gottenResult = `<p>Fehlerausgabe: <pre>${result.gotten}</pre></p>`;
    } else {
        gottenResult = `<p>Bekommen: <code>${printValue(result.gotten)}</code></p>`;
    }

    return `
<div class="card my-3">
    <div class="card-header text-${result.correct ? 'success' : 'danger'}">${result.id}. Test war ${result.correct ? '' : ' nicht'} erfolgreich.</div>
    <div class="card-body">
        <p>Eingabe: <code>${printValue(result.input)}</code></p>
        <p>Erwartet: <code>${printValue(result.awaited)}</code></p>
        ${gottenResult}
        ${consoleOut}
    </div>
</div>`.trim();
}


function renderProgCorrectionSuccess(response: ProgCorrectionResult): string {

    let html: string = `<div class="text-${response.solutionSaved ? 'success' : 'danger'}">Ihre Lösung wurde ${response.solutionSaved ? '' : ' nicht'} gespeichert.</div>`;

    const itemsPerRow = 1;
    const colWidth = 12 / itemsPerRow;

    for (let outerCount = 0; outerCount < response.results.length; outerCount = outerCount + itemsPerRow) {

        for (let innerCount = outerCount; innerCount < outerCount + itemsPerRow && innerCount < response.results.length; innerCount++) {
            const currentResult = response.results[innerCount] || null;
            html += currentResult != null ? renderProgResult(currentResult) : '';
        }

    }

    return html;
}
