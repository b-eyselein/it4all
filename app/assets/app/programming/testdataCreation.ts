import {domReady, testExerciseSolution} from "../otherHelpers";
import {ProgSolution, TestData, TestDataCreationResult, TestDataInput} from "./progCorrectionHandler";

let testBtn: HTMLButtonElement;
let moreTestDataBtn: HTMLButtonElement;

let testDataBody: HTMLElement;
let msgDiv: HTMLDivElement;

let inputCount: number;

function moreTestData(): void {
    const newTestId: number = testDataBody.querySelectorAll<HTMLTableRowElement>('tr').length;

    let inputs: string = '';
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

function onValidateTDSuccess(response: TestDataCreationResult): void {
    testBtn.disabled = false;

    console.warn(JSON.stringify(response, null, 2));

    if (response.solutionSaved) {
        msgDiv.innerHTML = `<hr><div class="alert alert-success">Ihre Testdaten wurden gespeichert.</div>`;
    } else {
        msgDiv.innerHTML = `<hr><div class="alert alert-danger">Ihre Testdaten konnten nicht gespeichert werden!</div>`;
    }

    for (let data of response.results) {
        let tableRow = document.querySelector<HTMLTableRowElement>('#tr_' + data.id);
        tableRow.classList.remove('success', 'danger', 'warning');
        tableRow.classList.add(data.correct ? 'success' : 'danger');
    }
}

function testSol(): void {
    let testData: TestData[] = [];

    testDataBody
        .querySelectorAll<HTMLTableRowElement>('tr')
        .forEach((elem: HTMLTableRowElement) => {
            const id: number = parseInt(elem.dataset['testid']);

            const inputs: TestDataInput[] = Array.from(elem.querySelectorAll<HTMLInputElement>('td > input[name^="inp"'))
                .map((e: HTMLInputElement, id: number) => ({id, input: e.value}));

            const output: string = elem
                .querySelector<HTMLInputElement>('td > input[name^="outp"]')
                .value as string;

            if (output.length === 0) {
                elem.classList.add('warning');
                elem.title = 'Output ist leer, Zeile wird ignoriert!';
            } else {
                testData.push({id, inputs, output});
            }
        });

    const solution: ProgSolution = {
        implementation: '',
        testData,
        unitTest: {name: '', content: '', fileType: 'python', editable: false}
    };

    testExerciseSolution<ProgSolution, TestDataCreationResult>(testBtn, solution, onValidateTDSuccess);
}

domReady(() => {
    inputCount = parseInt(document.querySelector<HTMLInputElement>('#inputCount').value);

    msgDiv = document.querySelector<HTMLDivElement>('#messageDiv');
    testDataBody = document.querySelector<HTMLElement>('#testDataBody');

    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;

    moreTestDataBtn = document.querySelector<HTMLButtonElement>('#moreTestDataBtn');
    moreTestDataBtn.onclick = moreTestData;
});
