import {BoolSolution, readBoolSolution} from "./boolBase";
import {domReady, testExerciseSolution} from "../otherHelpers";

let valueTableBody: HTMLElement;
let testBtn: HTMLButtonElement;

interface BoolFilloutResult {
    isSuccessful: boolean
    assignments: BoolFilloutRow[]
}

interface BoolFilloutRow {
    id: string
    learner: boolean
    sample: boolean
}

function changeValue(button: HTMLButtonElement): void {
    let newValue = (parseInt(button.innerText) + 1) % 2;

    button.innerText = newValue.toString();
    if (newValue === 0) {
        button.classList.remove('btn-primary');
        button.classList.add('btn-outline-primary');
    } else {
        button.classList.remove('btn-outline-primary');
        button.classList.add('btn-primary');
    }
}

function onFilloutCorrectionSuccess(respone: BoolFilloutResult): void {
    testBtn.disabled = false;

    for (let row of respone.assignments) {
        const elem: HTMLTableRowElement = document.querySelector<HTMLTableRowElement>('#' + row.id);

        if (row.learner === row.sample) {
            elem.classList.remove('table-danger');
            elem.classList.add('table-success');
            elem.querySelector('.correctnessHook').innerHTML = '&check;';
        } else {
            elem.classList.remove('table-success');
            elem.classList.add('table-danger');
            elem.querySelector('.correctnessHook').innerHTML = '';
        }
    }
}

function testSol(): void {
    testBtn.disabled = true;

    const solution = readBoolSolution(valueTableBody, true);

    testExerciseSolution<BoolSolution, BoolFilloutResult>(testBtn, solution, onFilloutCorrectionSuccess)
}

domReady(() => {
    valueTableBody = document.querySelector('#valueTableBody')
    valueTableBody.querySelectorAll<HTMLButtonElement>('button')
        .forEach(button => button.onclick = (event) => changeValue(event.target as HTMLButtonElement));

    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;
});
