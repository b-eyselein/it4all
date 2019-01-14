import * as $ from 'jquery';

import {readBoolSolution} from "./boolBase";

let valueTableBody: JQuery, testBtn: JQuery;

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
    let jButton = $(button);
    let newValue = (parseInt(jButton.text()) + 1) % 2;

    jButton.text(newValue);
    if (newValue === 0) {
        jButton.removeClass('btn-primary').addClass('btn-outline-primary');
    } else {
        jButton.removeClass('btn-outline-primary').addClass('btn-primary');
    }
}

function onFilloutCorrectionError(jqXHR): void {
    testBtn.prop('disabled', false);
    console.error(jqXHR.responseText);
}

function onFilloutCorrectionSuccess(respone: BoolFilloutResult): void {
    testBtn.prop('disabled', false);

    for (let row of respone.assignments) {
        let elem = $('#' + row.id);

        if (row.learner === row.sample) {
            elem.removeClass('table-danger').addClass('table-success');
            elem.find('.correctnessHook').html('&check;');
        } else {
            elem.removeClass('table-success').addClass('table-danger');
            elem.find('.correctnessHook').html('');
        }
    }
}

function testSol(): void {
    testBtn.prop('disabled', true);

    const solution = readBoolSolution(valueTableBody, true);

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: testBtn.data('url'),
        data: JSON.stringify(solution),
        async: true,
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token);
        },
        success: onFilloutCorrectionSuccess,
        error: onFilloutCorrectionError
    });
}

$(() => {
    valueTableBody = $('#valueTableBody');
    valueTableBody.find('button').on('click', (event) => changeValue(event.target as HTMLButtonElement));

    testBtn = $('#testBtn');
    testBtn.on('click', testSol);
});