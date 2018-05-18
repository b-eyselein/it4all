import * as $ from 'jquery';

export {onProgCorrectionError, onProgCorrectionSuccess};

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
<div class="panel panel-${result.correct ? 'success' : 'danger'}">
    <div class="panel-heading">${result.id}. Test war ${result.correct ? '' : ' nicht'} erfolgreich.</div>
    <div class="panel-body">
        <p>Eingabe: <pre>${printValue(result.input)}</pre></p>
        <p>Erwartet: <code>${printValue(result.awaited)}</code></p>
        ${gottenResult}
        ${consoleOut}
    </div>
</div>`.trim();
}


interface ProgCorrectionResult {
    solutionSaved: boolean
    results: ProgSingleResult[]
}

function onProgCorrectionSuccess(response: ProgCorrectionResult): void {
    console.warn(JSON.stringify(response, null, 2));

    $('#correctionDiv').prop('hidden', false);

    let html = `<div class="alert alert-${response.solutionSaved ? 'success' : 'danger'}">Ihre Lösung wurde ${response.solutionSaved ? '' : ' nicht'} gespeichert.</div>`;

    for (let i = 0; i < response.results.length; i = i + 3) {
        let firstResult = response.results[i] || null;
        let secondResult = response.results[i + 1] || null;
        let thirdNextResult = response.results[i + 2] || null;

        html += `
<div class="row">
    <div class="col-md-4">${firstResult != null ? renderProgResult(firstResult) : ''}</div>
    <div class="col-md-4">${secondResult != null ? renderProgResult(secondResult) : '' }</div>
    <div class="col-md-4">${thirdNextResult != null ? renderProgResult(thirdNextResult) : ''}</div>
</div>`.trim();

    }
    $('#correction').html(html);
    $('#testBtn').prop('disabled', false);
}

function onProgCorrectionError(jqXHR): void {
    console.error(jqXHR.responseText);
    $('#testBtn').prop('disabled', false);
}