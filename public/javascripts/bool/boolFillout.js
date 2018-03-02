function readValues() {
    let lerVar = $('#lerVar').text();

    let assignments = [];

    // FIXME: map instead of each
    $('#valueTableBody').find('tr').each(function (index, row) {
        let partAssignments = {};


        // FIXME: map instead of each
        $(row).find('.text-center').each(function (index, cell) {
            partAssignments[cell.dataset.variable] = (cell.dataset.value === 'true')
        });

        partAssignments[lerVar] = $(row).find('button').text() === '1';
        assignments.push(partAssignments)
    });

    return {
        formula: $('#formula').data('formula'),
        assignments
    };
}

function changeValue(button) {
    let buttonJQ = $(button);
    let newValue = (parseInt(buttonJQ.text()) + 1) % 2;

    buttonJQ.text(newValue);
    if (newValue === 0) {
        buttonJQ.removeClass('btn-primary').addClass('btn-default');
    } else {
        buttonJQ.removeClass('btn-default').addClass('btn-primary');
    }
}

function onAjaxError(jqXHR) {
    console.error(jqXHR.responseText);
    $('#testBtn').prop('disabled', false);
}

/**
 * @param {object[]} rows
 * @param {string} rows.id
 * @param {object.<String, boolean>} rows.assignments
 */
function onAjaxSuccess(rows) {
    let lerVar = $('#lerVar').text();
    let solVar = $('#solVar').val();

    for (let row of rows) {
        let elem = $('#' + row.id);

        if (row.assignments[lerVar] === row.assignments[solVar]) {
            elem.removeClass('danger').addClass('success');
        } else {
            elem.removeClass('success').addClass('danger');
        }
    }

    $('#testBtn').prop('disabled', false);
}

function testSol() {
    let toolType = $('#toolType').val(), exerciseType = $('#exerciseType').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.exes.RandomExerciseController.correctLive(toolType, exerciseType).url;

    $('#testBtn').prop('disabled', true);

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url,
        data: JSON.stringify(readValues()),
        async: true,
        success: onAjaxSuccess,
        error: onAjaxError
    });
}

$(document).ready(function () {
    $('#testBtn').click(testSol);
});