import * as $ from 'jquery';

let dnf: string = '';
let knf: string = '';

let testBtn: JQuery, sampleSolBtn: JQuery, valueTableBody: JQuery;

interface BoolCreateSolution {
    solution: string
    assignments: object[]
}

interface AssignmentSolution {
    id: string
    learnerVal: boolean
    correct: boolean
}

interface BoolCreateResult {
    assignments: AssignmentSolution[]

    knf: string
    dnf: string
}


function readValues(): BoolCreateSolution {
    let solution: string = $('#solution').val() as string;

    if (solution == null || solution.length === 0) {
        return null;
    }

    let assignments = [];

    valueTableBody.find('tr').each((index: number, row: HTMLElement) => {
        let partAssignments = {};

        $(row).find('[data-variable]').each(function (index, cell) {
            partAssignments[cell.dataset.variable] = cell.dataset.value === 'true'
        });

        assignments.push(partAssignments);
    });

    return {solution, assignments};
}


function onBoolCreationSuccess(response: BoolCreateResult): void {
    testBtn.prop('disabled', false);

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

function onBoolCreationError(jqXHR) {
    testBtn.prop('disabled', false);
    console.error(jqXHR.responseText);
}

function testSol(): void {

    let solution = readValues();

    if (solution == null) {
        alert('Sie können keine leere Formel abgeben!');
        return;
    }

    testBtn.prop('disabled', true);

    console.warn(JSON.stringify(solution, null, 2));

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: testBtn.data('url'),
        data: JSON.stringify(solution),
        async: true,
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