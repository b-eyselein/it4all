import * as $ from 'jquery';
import {domReady} from "../otherHelpers";

let testBtn: JQuery, moreTestDataBtn: JQuery, testDataBody: JQuery, msgDiv: JQuery;
let inputCount: number;

function moreTestData(): void {
    const newTestId = testDataBody.find('tr').length;

    let inputs = '';
    for (let ic = 0; ic < inputCount; ic++) {
        inputs += `<td><input class="form-control" name="inp_${ic}_${newTestId}" id="inp_${ic}_${newTestId}" placeholder="Test ${newTestId + 1}, Input ${ic + 1}"></td>`;
    }

    testDataBody.append(
        `
<tr id="tr_${newTestId}" data-testid="${newTestId}">
    <td>${newTestId}</td>
    ${inputs}
    <td><input class="form-control" name="outp_${newTestId}" id="outp_${newTestId}" placeholder="Test ${newTestId + 1}, Output"></td>
</tr>`.trim());
}

interface TestDataResult {
    id: number
    correct: boolean
}

interface TestdataCreationResult {
    solutionSaved: boolean
    results: TestDataResult[]
}

function onValidateTDSuccess(response: TestdataCreationResult): void {
    testBtn.prop('disabled', false);

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

function onValidateTDError(jqXHR): void {
    testBtn.prop('disabled', false);
    console.error(jqXHR.responseText);
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

function testSol(): void {
    testBtn.prop('disabled', true);

    let solution: TestData[] = [];

    testDataBody.find('tr')
        .removeClass('success danger warning')
        .each((index, elem: HTMLTableRowElement) => {
            const id: number = $(elem).data('testid');

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

    $.ajax({
        type: 'PUT',
        // dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: testBtn.data('url'),
        data: JSON.stringify({solution}),
        async: true,
        success: onValidateTDSuccess,
        error: onValidateTDError
    });
}

domReady(() => {
    inputCount = $('#inputCount').val() as number;

    msgDiv = $('#messageDiv');
    testDataBody = $('#testDataBody');

    testBtn = $('#testBtn');
    testBtn.on('click', testSol);

    moreTestDataBtn = $('#moreTestDataBtn');
    moreTestDataBtn.on('click', moreTestData);
});
