import {CorrectionResult} from "../matches";
import {SuccessType} from "../otherHelpers";
import {ExerciseFile} from "../tools/ideExerciseHelpers";

export interface ProgSolution {
    implementation: string;
    testData: TestData[];
    unitTest: ExerciseFile;
}

interface ProgSingleResult {
    id: number
    success: SuccessType;
    // correct: boolean
    input: object
    awaited: string
    gotten: string
    stdout: string | null
}


export interface TestDataResult {
    id: number;
    successType: 'ERROR' | '';
    correct: boolean;
    input: TestDataInput[];
    output: string;
    awaited: string;
    gotten: string;
}

export interface TestDataCreationResult {
    solutionSaved: boolean
    results: TestDataResult[]
}

export interface TestDataInput {
    id: number,
    input: string
}

export interface TestData {
    id: number
    inputs: TestDataInput[]
    output: string
}

export interface NormalExecutionResult {
    success: SuccessType;
    logs: string;
}

export interface UnitTestTestConfig {
    id: number;
    shouldFail: boolean;
    cause: null | string;
    description: string;
}

export interface UnitTestCorrectionResult {
    testConfig: UnitTestTestConfig;
    successful: boolean;
    file: string;
    stdout: string[];
    stderr: string[];
}

export interface ProgCorrectionResult extends CorrectionResult<ProgSingleResult> {
    simplifiedResults: ProgSingleResult[];
    normalResult: NormalExecutionResult;
    unitTestResults: UnitTestCorrectionResult[];
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
    if (result.stdout !== null) {
        consoleOut = '<p>Konsolenausgabe: <pre>' + result.stdout + '</pre></p>';
    }

    let gottenResult: string;
    if (result.success === "ERROR") {
        gottenResult = `<p>Fehlerausgabe: <pre>${result.gotten}</pre></p>`;
    } else {
        gottenResult = `<p>Bekommen: <code>${printValue(result.gotten)}</code></p>`;
    }

    const correct = result.success === 'COMPLETE';

    return `
<div class="card my-3">
    <div class="card-header text-${correct ? 'success' : 'danger'}">${result.id}. Test war ${correct ? '' : ' nicht'} erfolgreich.</div>
    <div class="card-body">
        <p>Eingabe: <code>${printValue(result.input)}</code></p>
        <p>Erwartet: <code>${printValue(result.awaited)}</code></p>
        ${gottenResult}
        ${consoleOut}
    </div>
</div>`.trim();
}


export function renderProgCorrectionSuccess(response: ProgCorrectionResult): string {

    console.info(JSON.stringify(response, null, 2));

    let html: string = '';

    if (response.solutionSaved) {
        html += `<p class="text-success">Ihre Lösung wurde gespeichert.</p>`;
    } else {
        html += `<p class="text-danger">Ihre Lösung konnte nicht gespeichert werden.</p>`;
    }

    // FIXME: send and display points...
    console.info(response.points, response.maxPoints);
    // html += `<p>Sie haben ${response.points} von ${response.maxPoints} erreicht.</p>`;

    if (response.simplifiedResults.length !== 0) {
        const successfulTests = response.simplifiedResults.filter(r => r.success === 'COMPLETE').length;

        html += `<p>Sie haben ${successfulTests} von ${response.simplifiedResults.length} Tests bestanden.</p>`;

        for (const currentResult of response.simplifiedResults) {
            html += renderProgResult(currentResult);
        }
    } else {
        const successful = response.normalResult.success === 'COMPLETE';

        html += `<p class="text-${successful ? 'success' : 'danger'}">Ihre Lösung hat die Tests ${successful ? '' : 'nicht '}bestanden.</p>`;

        html += `
<div class="card">
    <div class="card-header">Konsolenausgabe</div>
    <div class="card-body">
        <pre class="text-${successful ? 'success' : 'danger'}">${response.normalResult.logs}</pre>
    </div>
</div>`.trim();
    }

    return html;
}
