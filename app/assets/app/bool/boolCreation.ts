import * as $ from 'jquery';

import {BoolSolution, readBoolSolution} from "./boolBase";

let dnf: string = '';
let knf: string = '';

let testBtn: JQuery, sampleSolBtn: JQuery, valueTableBody: JQuery;

interface AssignmentSolution {
    id: string
    learnerVal: boolean
    correct: boolean
}

interface BoolCreateResult {
    isSuccessful: boolean
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
        let elem = $('#' + assignment.id);
        elem.html(assignment.learnerVal ? '1' : '0');
        if (assignment.correct) {
            elem.removeClass('table-danger').addClass('table-success');
        } else {
            elem.removeClass('table-success').addClass('table-danger');
        }
    }
}

function renderBoolCreationError(response: BoolCreationError): void {
    $('#messageDiv').html(`
<hr>

<div class="alert alert-danger">
    <p>Es gab einen Fehler in ihrer Formel:</p>
    <pre>${response.formula}</pre>
    <pre>${response.error}</pre>
</div>`.trim());
}

function onBoolCreationSuccess(response: BoolCreateResult): void {
    testBtn.prop('disabled', false);

    if (response.isSuccessful) {
        renderBoolCreationSuccess(response as BoolCreationSuccess);
    } else {
        renderBoolCreationError(response as BoolCreationError);
    }

}

function onBoolCreationError(jqXHR): void {
    testBtn.prop('disabled', false);
    console.error(jqXHR.responseText);
}

function testSol(): void {
    const solution: BoolSolution = readBoolSolution(valueTableBody, false);

    if (solution.formula == null || solution.formula.length === 0) {
        alert('Sie können keine leere Formel abgeben!');
        return;
    }

    $('#messageDiv').html('');
    testBtn.prop('disabled', true);

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
        success: onBoolCreationSuccess,
        error: onBoolCreationError
    });
}

function showSampleSol(): void {
    if (knf.length === 0 && dnf.length === 0) {
        alert('Sample solutions have not yet been loaded!');
    } else {
        $('#messageDiv').html(`
<hr>
<p>KNF: <code>z = ${knf}</code></p>
<p>DNF: <code>z = ${dnf}</code></p>`.trim());
    }
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testSol);

    sampleSolBtn = $('#sampleSolBtn');
    sampleSolBtn.on('click', showSampleSol);

    valueTableBody = $('#valueTableBody');
});