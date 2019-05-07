import {BoolSolution, readBoolSolution} from "./boolBase";
import {domReady, SuccessType, testExerciseSolution} from "../otherHelpers";

let dnf: string = '';
let knf: string = '';

let testBtn: HTMLButtonElement;
let sampleSolBtn: HTMLButtonElement;
let valueTableBody: HTMLElement;

interface AssignmentSolution {
    id: string
    learnerVal: boolean
    correct: boolean
}

interface BoolCreateResult {
    success: SuccessType
}

interface BoolCreationSuccess extends BoolCreateResult {
    assignments: AssignmentSolution[]
    knf: string
    dnf: string
}

interface BoolCreationError extends BoolCreateResult {
    formula: string
    error: string
}

function renderBoolCreationSuccess(response: BoolCreationSuccess): void {
    knf = response.knf;
    dnf = response.dnf;

    for (const assignment of response.assignments) {
        let elem = document.querySelector<HTMLElement>('#' + assignment.id);
        elem.innerHTML = assignment.learnerVal ? '1' : '0';

        if (assignment.correct) {
            elem.classList.remove('table-danger');
            elem.classList.add('table-success');
            elem.parentElement.querySelector('.correctnessHook').innerHTML = '&check;';
        } else {
            elem.classList.remove('table-success');
            elem.classList.add('table-danger');
            elem.parentElement.querySelector('.correctnessHook').innerHTML = '';
        }
    }
}

function renderBoolCreationError(response: BoolCreationError): void {
    document.querySelector<HTMLDivElement>('#messageDiv').innerHTML = `
<hr>

<div class="alert alert-danger">
    <p>Es gab einen Fehler in ihrer Formel:</p>
    <pre>${response.formula}</pre>
    <pre>${response.error}</pre>
</div>`.trim();
}

function onBoolCreationSuccess(response: BoolCreateResult): void {
    console.info(JSON.stringify(response, null, 2));

    switch (response.success) {
        case 'ERROR':
            renderBoolCreationError(response as BoolCreationError);
        default:
            renderBoolCreationSuccess(response as BoolCreationSuccess);
    }
}

function testSol(): void {
    const solution: BoolSolution = readBoolSolution(valueTableBody, false);

    if (solution.formula == null || solution.formula.length === 0) {
        alert('Sie können keine leere Formel abgeben!');
        return;
    }

    document.querySelector<HTMLDivElement>('#messageDiv').innerHTML = '';

    testExerciseSolution<BoolSolution, BoolCreateResult>(testBtn, solution, onBoolCreationSuccess);
}

function showSampleSol(): void {
    if (knf.length === 0 && dnf.length === 0) {
        alert('Sample solutions have not yet been loaded!');
    } else {
        document.querySelector<HTMLDivElement>('#messageDiv').innerHTML = `
<hr>
<p>KNF: <code>z = ${knf}</code></p>
<p>DNF: <code>z = ${dnf}</code></p>`.trim();
    }
}

domReady(() => {
    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;

    sampleSolBtn = document.querySelector<HTMLButtonElement>('#sampleSolBtn');
    sampleSolBtn.onclick = showSampleSol;

    valueTableBody = document.querySelector<HTMLElement>('#valueTableBody');
});
