/**
 *
 * @param {{boolean}} withSol
 *
 * @returns {{solution: (*|jQuery), assignments: Array}}
 */
function readValues(withSol) {
    let assignments = [];

    $('#valueTableBody').find('tr').each(function (index, row) {
        let partAssignments = {};
        $(row).find('[data-variable]').each(function (index, cell) {
            partAssignments[cell.dataset.variable] = cell.dataset.value === 'true'
        });
        assignments.push(partAssignments);
    });

    return {
        solution: $('#solution').val(),
        assignments: assignments,
        withSol: withSol
    };
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
 * @param {Object} response.muster
 * @param {string} response.muster.knf
 * @param {string} response.muster.dnf
 *
 * @param {boolean} withSol
 */
function onAjaxSuccess(response, withSol) {
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
        $('#messageDiv').html('<hr><p>KNF: z = ' + response.muster.knf + '</p><p>DNF: z = ' + response.muster.dnf + '</p>');
    }
}

/**
 * @param {{string}} theUrl
 * @param {{boolean}} withSol
 */
function testSol(theUrl, withSol) {
    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: theUrl,
        data: JSON.stringify(readValues(withSol)),
        async: true,
        success: function (response) {
            onAjaxSuccess(response, withSol)
        }
    });
}