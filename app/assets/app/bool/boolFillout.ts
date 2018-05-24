import * as $ from 'jquery';

let valueTableBody: JQuery, testBtn: JQuery;

interface BoolAssignment {
//TODO...
}

interface BoolFilloutSolution {
    formula: string
    assignments: BoolAssignment[]
}

interface BoolFilloutResult {
    id: string
    learner: boolean
    sample: boolean
}

function readValues(): BoolFilloutSolution {
    let lerVar = $('#lerVar').text();

    let assignments: BoolAssignment[] = [];

    // FIXME: map instead of each
    valueTableBody.find('tr').each((index: number, row: HTMLElement) => {
        let partAssignments = {};

        // FIXME: map instead of each
        $(row).find('.text-center').each(function (index, cell) {
            partAssignments[cell.dataset.variable] = (cell.dataset.value === 'true')
        });

        partAssignments[lerVar] = $(row).find('button').text() === '1';
        assignments.push(partAssignments)
    });

    return {formula: $('#formula').data('formula'), assignments};
}

function changeValue(button: HTMLButtonElement): void {
    let jButton = $(button);
    let newValue = (parseInt(jButton.text()) + 1) % 2;

    jButton.text(newValue);
    if (newValue === 0) {
        jButton.removeClass('btn-primary').addClass('btn-default');
    } else {
        jButton.removeClass('btn-default').addClass('btn-primary');
    }
}

function onAjaxError(jqXHR): void {
    console.error(jqXHR.responseText);
    testBtn.prop('disabled', false);
}

function onAjaxSuccess(rows: BoolFilloutResult[]): void {
    for (let row of rows) {
        let elem = $('#' + row.id);

        if (row.learner === row.sample) {
            elem.removeClass('table-danger').addClass('table-success');
        } else {
            elem.removeClass('table-success').addClass('table-danger');
        }
    }

    testBtn.prop('disabled', false);
}

function testSol(): void {
    testBtn.prop('disabled', true);

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: testBtn.data('url'),
        data: JSON.stringify(readValues()),
        async: true,
        success: onAjaxSuccess,
        error: onAjaxError
    });
}

$(() => {
    valueTableBody = $('#valueTableBody');
    valueTableBody.find('button').on('click', (event) => changeValue(event.target as HTMLButtonElement));

    testBtn = $('#testBtn');
    testBtn.on('click', testSol);
});