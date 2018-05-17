import * as $ from 'jquery';

interface BoolAssignment {
//TODO...
}

interface BoolFilloutSolution {
    formula: string
    assignments: BoolAssignment[]
}

function readValues(): BoolFilloutSolution {
    let lerVar = $('#lerVar').text();

    let assignments: BoolAssignment[] = [];

    // FIXME: map instead of each
    $('#valueTableBody').find('tr').each((index: number, row: HTMLElement) => {
        let partAssignments = {};


        // FIXME: map instead of each
        $(row).find('.text-center').each(function (index, cell) {
            partAssignments[cell.dataset.variable] = (cell.dataset.value === 'true')
        });

        partAssignments[lerVar] = $(row).find('button').text() === '1';
        assignments.push(partAssignments)
    });

    console.warn(JSON.stringify(assignments, null, 2));

    return {
        formula: $('#formula').data('formula'),
        assignments
    };
}

function changeValue(button: HTMLButtonElement): void {
    let buttonJQ = $(button);
    let newValue = (parseInt(buttonJQ.text()) + 1) % 2;

    buttonJQ.text(newValue);
    if (newValue === 0) {
        buttonJQ.removeClass('btn-primary').addClass('btn-default');
    } else {
        buttonJQ.removeClass('btn-default').addClass('btn-primary');
    }
}

function onAjaxError(jqXHR): void {
    console.error(jqXHR.responseText);
    $('#testBtn').prop('disabled', false);
}

interface BoolFilloutResult {
    id: string
    learner: boolean
    sample: boolean
}

/**
 * @param {object[]} rows
 * @param {string} rows.id
 * @param {object.<String, boolean>} rows.assignments
 */
function onAjaxSuccess(rows: BoolFilloutResult[]): void {
    let lerVar = $('#lerVar').text() as string;
    let solVar = $('#solVar').val() as string;

    for (let row of rows) {
        let elem = $('#' + row.id);

        if (row.learner === row.sample) {
            elem.removeClass('danger').addClass('success');
        } else {
            elem.removeClass('success').addClass('danger');
        }
    }

    $('#testBtn').prop('disabled', false);
}

function testSol(): void {
    let testBtn = $('#testBtn');
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
    $('#valueTableBody').find('button').on('click', (event) => changeValue(event.target as HTMLButtonElement));

    $('#testBtn').on('click', testSol);
});