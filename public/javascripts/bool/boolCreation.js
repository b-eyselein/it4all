/**
 *
 * @param {boolean} withSol
 *
 * @returns {object | null}
 */
function readValues(withSol) {
    let solution = $('#solution').val();

    if (solution == null || solution.length === 0) {
        return null;
    }

    let assignments = [];

    $('#valueTableBody').find('tr').each(function (index, row) {
        let partAssignments = {};
        $(row).find('[data-variable]').each(function (index, cell) {
            partAssignments[cell.dataset.variable] = cell.dataset.value === 'true'
        });
        assignments.push(partAssignments);
    });

    return {solution, assignments, withSol};
}

/**
 *
 * @param {Object} response
 *
 * @param {Object[]} response.assignments
 * @param {boolean} response.assignments[].correct
 * @param {boolean} response.assignments[].learnerVal
 * @param {string} response.assignments[].id
 *
 * @param {string} response.knf
 * @param {string} response.dnf
 *
 * @param {boolean} withSol
 */
function onAjaxSuccess(response, withSol) {
    console.log(withSol);
    console.log(JSON.stringify(response, null, 2));

    for (const assignment of response.assignments) {
        let elem = $('#' + assignment.id);
        elem.html(assignment.learnerVal ? '1' : '0');
        if (assignment.correct) {
            elem.removeClass('danger').addClass('success');
        } else {
            elem.removeClass('success').addClass('danger');
        }
    }

    if (withSol) {
        $('#messageDiv').html('<hr><p>KNF: z = ' + response.knf + '</p><p>DNF: z = ' + response.dnf + '</p>');
    }

    $('#testBtn').prop('disabled', false);
    $('#correctBtn').prop('disabled', false);
}

function onAjaxError(jqXHR) {
    console.error(jqXHR.responseText);

    $('#testBtn').prop('disabled', false);
    $('#correctBtn').prop('disabled', false);
}

/**
 * @param {boolean} withSol
 */
function testSol(withSol) {
    let toolType = $('#toolType').val(), exerciseType = $('#exerciseType').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.RandomExerciseController.correctLive(toolType, exerciseType).url;

    let solution = readValues(withSol);

    if (solution == null) {
        alert('Sie können keine leere Formel abgeben!');
        return;
    }

    $('#testBtn').prop('disabled', true);
    $('#correctBtn').prop('disabled', true);

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url,
        data: JSON.stringify(readValues(withSol)),
        async: true,
        success: function (response) {
            onAjaxSuccess(response, withSol)
        }
    });
}

$(document).ready(function () {
    $('#testBtn').click(() => testSol(false));
    $('#correctBtn').click(() => testSol(true));
});