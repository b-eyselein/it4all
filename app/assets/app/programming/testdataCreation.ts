import * as $ from 'jquery';
import {domReady, testExerciseSolution} from "../otherHelpers";

let testBtn: HTMLButtonElement;
let moreTestDataBtn: HTMLButtonElement;

let testDataBody: HTMLElement;
let msgDiv: JQuery;

let inputCount: number;

interface TestDataResult {
    id: number
    correct: boolean
}

interface TestdataCreationResult {
    solutionSaved: boolean
    results: TestDataResult[]
}

interface TestDataInput {
    id: number,
    input: string
}

interface TestData {
    id: number
    inputs: TestDataInput[]
    output: string
}

function moreTestData(): void {
    const newTestId = testDataBody.querySelectorAll<HTMLTableRowElement>('tr').length;

    let inputs = '';
    for (let ic = 0; ic < inputCount; ic++) {
        inputs += `<td><input class="form-control" name="inp_${ic}_${newTestId}" id="inp_${ic}_${newTestId}" placeholder="Test ${newTestId + 1}, Input ${ic + 1}"></td>`;
    }

    testDataBody.innerHTML += `
<tr id="tr_${newTestId}" data-testid="${newTestId}">
    <td>${newTestId}</td>
    ${inputs}
    <td><input class="form-control" name="outp_${newTestId}" id="outp_${newTestId}" placeholder="Test ${newTestId + 1}, Output"></td>
</tr>`.trim();
}

function onValidateTDSuccess(response: TestdataCreationResult): void {
    testBtn.disabled = false;

    console.warn(JSON.stringify(response, null, 2));

    if (response.solutionSaved) {
        msgDiv.html(`<hr><div class="alert alert-success">Ihre Testdaten wurden gespeichert.</div>`);
    } else {
        msgDiv.html(`<hr><div class="alert alert-danger">Ihre Testdaten konnten nicht gespeichert werden!</div>`);
    }

    for (let data of response.results) {
        let jTableRow = $('#tr_' + data.id);
        if (data.correct) {
            jTableRow.removeClass('danger').addClass('success');
        } else {

            jTableRow.removeClass('success').addClass('danger');
        }
    }
}

function testSol(): void {
    testBtn.disabled = true;

    let solution: TestData[] = [];

    const tableRows = testDataBody.querySelectorAll<HTMLTableRowElement>('tr');

    tableRows.forEach(tr => tr.classList.remove('success', 'danger', 'warning'));

    tableRows.forEach((elem: HTMLTableRowElement) => {
        const id: number = parseInt(elem.dataset['testid']);

        const inputs: TestDataInput[] = [];
        $(elem).find('td input').filter((i, e: Element) =>
            e instanceof HTMLInputElement && e.name.startsWith('inp')
        ).each((id, e: Element) => {
            inputs.push({id, input: (e as HTMLInputElement).value});
        });

        const output: string = $(elem).find('td input')
            .filter((i, e: Element) => e instanceof HTMLInputElement && e.name.startsWith('outp')).val() as string;

        if (output.length === 0) {
            $(elem).addClass('warning');
            $(elem).attr('title', 'Output ist leer, Zeile wird ignoriert!');
        } else {
            solution.push({id, inputs, output});
        }
    });

    console.warn(JSON.stringify(solution, null, 2));

    testExerciseSolution<TestData[], TestdataCreationResult>(testBtn, solution, onValidateTDSuccess);
}

domReady(() => {
    inputCount = parseInt(document.querySelector<HTMLInputElement>('#inputCount').value);

    msgDiv = $('#messageDiv');
    testDataBody = document.querySelector<HTMLElement>('#testDataBody');

    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;

    moreTestDataBtn = document.querySelector<HTMLButtonElement>('#moreTestDataBtn');
    moreTestDataBtn.onclick = moreTestData;
});
